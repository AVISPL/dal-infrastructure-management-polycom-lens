/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.polycom.lens;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.api.dal.monitor.aggregator.Aggregator;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.infrastructure.management.polycom.lens.common.PolyLensConstant;
import com.avispl.symphony.dal.infrastructure.management.polycom.lens.common.PolyLensProperties;
import com.avispl.symphony.dal.infrastructure.management.polycom.lens.common.PolyLensSystemInfoMetric;
import com.avispl.symphony.dal.infrastructure.management.polycom.lens.dto.SystemInformation;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * Poly Lens Communicator
 * Supported features are:
 * Monitoring Aggregated Device:
 * Controlling:
 */
public class PolyLensCommunicator extends RestCommunicator implements Aggregator, Monitorable, Controller {
	/**
	 * Process that is running constantly and triggers collecting data from PoLy Lens API endpoints, based on the given timeouts and thresholds.
	 *
	 * @author Harrry
	 * @since 1.0.0
	 */
	class PolyLensDataLoader implements Runnable {
		private volatile boolean inProgress;
		private volatile int threadIndex = 0;

		public PolyLensDataLoader() {
			inProgress = true;
		}

		@Override
		public void run() {
			loop:
			while (inProgress) {
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					// Ignore for now
				}

				if (!inProgress) {
					break loop;
				}

				// next line will determine whether Poly Lens monitoring was paused
				updateAggregatorStatus();
				if (devicePaused) {
					continue loop;
				}
				long currentTimestamp = System.currentTimeMillis();
				if (logger.isDebugEnabled()) {
					logger.debug("Fetching other than PoLy Lens device list");
				}
				if (threadIndex < threadCount && nextDevicesCollectionIterationTimestamp <= currentTimestamp) {
					threadIndex++;
					populateDeviceDetails();
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					System.out.println(aggregatedDeviceList.size());
				}

				if (!inProgress) {
					break loop;
				}

				int aggregatedDevicesCount = aggregatedDeviceList.size();
				if (aggregatedDevicesCount == 0) {
					continue loop;
				}

				while (nextDevicesCollectionIterationTimestamp > System.currentTimeMillis()) {
					try {
						TimeUnit.MILLISECONDS.sleep(1000);
					} catch (InterruptedException e) {
						//
					}
				}
				if (threadIndex == threadCount) {
					threadIndex = 0;
					nextDevicesCollectionIterationTimestamp = System.currentTimeMillis() + 60000;
				}

				if (logger.isDebugEnabled()) {
					logger.debug("Finished collecting devices statistics cycle at " + new Date());
				}
			}
			// Finished collecting
		}

		/**
		 * Triggers main loop to stop
		 */
		public void stop() {
			inProgress = false;
		}
	}

	private final ReentrantLock reentrantLock = new ReentrantLock();
	ObjectMapper objectMapper = new ObjectMapper();
	private String loginHost = "https://login.silica-prod01.io.lens.poly.com/oauth/token";
	private PolyLensDataLoader deviceDataLoader;

	/**
	 * List of aggregated device
	 */
	private List<AggregatedDevice> aggregatedDeviceList = Collections.synchronizedList(new ArrayList<>());

	/**
	 * We don't want the statistics to be collected constantly, because if there's not a big list of devices -
	 * new devices' statistics loop will be launched before the next monitoring iteration. To avoid that -
	 * this variable stores a timestamp which validates it, so when the devices' statistics is done collecting, variable
	 * is set to currentTime + 30s, at the same time, calling {@link #retrieveMultipleStatistics()} and updating the
	 * {@link #aggregatedDeviceList} resets it to the currentTime timestamp, which will re-activate data collection.
	 */
	private long nextDevicesCollectionIterationTimestamp;

	/**
	 * Number of threads used in a poly internal
	 */
	private int threadCount;
	private int pageSize = 99;

	/**
	 * save time get token
	 */
	private Long tokenExpire;

	/**
	 * time the token expires
	 */
	private Long expiresIn;

	/**
	 * Poly Lens API Token
	 */
	private String apiToken;

	/**
	 * Executor that runs all the async operations, that is posting and
	 */
	private ExecutorService executorService;

	/**
	 * This parameter holds timestamp of when we need to stop performing API calls
	 * It used when device stop retrieving statistic. Updated each time of called #retrieveMultipleStatistics
	 */
	private volatile long validRetrieveStatisticsTimestamp;

	/**
	 * Aggregator inactivity timeout. If the {@link PolyLensCommunicator#retrieveMultipleStatistics()}  method is not
	 * called during this period of time - device is considered to be paused, thus the Cloud API
	 * is not supposed to be called
	 */
	private static final long retrieveStatisticsTimeOut = 3 * 60 * 1000;

	/**
	 * List of System Response
	 */
	private SystemInformation systemInformation = new SystemInformation();

	/**
	 * Indicates whether a device is considered as paused.
	 * True by default so if the system is rebooted and the actual value is lost -> the device won't start stats
	 * collection unless the {@link PolyLensCommunicator#retrieveMultipleStatistics()} method is called which will change it
	 * to a correct value
	 */
	private volatile boolean devicePaused = true;

	/**
	 * Update the status of the device.
	 * The device is considered as paused if did not receive any retrieveMultipleStatistics()
	 * calls during {@link PolyLensCommunicator}
	 */
	private synchronized void updateAggregatorStatus() {
		devicePaused = validRetrieveStatisticsTimestamp < System.currentTimeMillis();
	}

	/**
	 * Uptime time stamp to valid one
	 */
	private synchronized void updateValidRetrieveStatisticsTimestamp() {
		validRetrieveStatisticsTimestamp = System.currentTimeMillis() + retrieveStatisticsTimeOut;
		updateAggregatorStatus();
	}

	/**
	 * {@inheritDoc}
	 * get System Information of Aggregator Adapter
	 */
	@Override
	public List<Statistics> getMultipleStatistics() {
		reentrantLock.lock();
		try {
			if (!checkValidApiToken()) {
				throw new ResourceNotReachableException("API Token cannot be null or empty, please enter valid API token in the password and username field.");
			}
			Map<String, String> statistics = new HashMap<>();
			ExtendedStatistics extendedStatistics = new ExtendedStatistics();
			retrieveSystemInfo();
			populateSystemData(statistics);
			extendedStatistics.setStatistics(statistics);
			return Collections.singletonList(extendedStatistics);
		} finally {
			reentrantLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 * get information of aggregated device
	 */
	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics() {
		reentrantLock.lock();
		try {
			if (systemInformation.getCountDevices() != null) {
				threadCount = 1;
				if (systemInformation.getCountDevices() > pageSize) {
					threadCount = 2;
				}
				if (checkValidApiToken()) {
					if (executorService == null) {
						executorService = Executors.newFixedThreadPool(1);
						executorService.submit(deviceDataLoader = new PolyLensDataLoader());
					}
					nextDevicesCollectionIterationTimestamp = System.currentTimeMillis();
					updateValidRetrieveStatisticsTimestamp();
				}

				return aggregatedDeviceList;
			}
			return aggregatedDeviceList;
		} finally {
			reentrantLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics(List<String> listDeviceId) {
		return retrieveMultipleStatistics().stream().filter(aggregatedDevice -> listDeviceId.contains(aggregatedDevice.getDeviceId())).collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 * This method is recalled by Symphony to control specific property
	 *
	 * @param controllableProperty This is the property to be controlled
	 */
	@Override
	public void controlProperty(ControllableProperty controllableProperty) throws Exception {
		//Todo
	}

	/**
	 * {@inheritDoc}
	 * This method is recalled by Symphony to control a list of properties
	 *
	 * @param controllableProperties This is the list of properties to be controlled
	 */
	@Override
	public void controlProperties(List<ControllableProperty> controllableProperties) {
		if (CollectionUtils.isEmpty(controllableProperties)) {
			throw new IllegalArgumentException("ControllableProperties can not be null or empty");
		}
		for (ControllableProperty p : controllableProperties) {
			try {
				controlProperty(p);
			} catch (Exception e) {
				logger.error(String.format("Error when control property %s", p.getProperty()), e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void authenticate() {
		/**
		 * TODO
		 */
	}

	/**
	 * {@inheritDoc}
	 * set Bearer Token into Header of Request
	 */
	@Override
	protected HttpHeaders putExtraRequestHeaders(HttpMethod httpMethod, String uri, HttpHeaders headers) {
		headers.setBearerAuth(apiToken);
		return headers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void internalInit() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Internal init is called.");
		}
		executorService = Executors.newFixedThreadPool(1);
		executorService.submit(deviceDataLoader = new PolyLensDataLoader());
		super.internalInit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void internalDestroy() {
		if (logger.isDebugEnabled()) {
			logger.debug("Internal destroy is called.");
		}

		if (deviceDataLoader != null) {
			deviceDataLoader.stop();
			deviceDataLoader = null;
		}

		if (executorService != null) {
			executorService.shutdownNow();
			executorService = null;
		}

		aggregatedDeviceList.clear();
		super.internalDestroy();
	}

	/**
	 * Check API token validation
	 * If the token expires, we send a request to get a new token
	 *
	 * @return boolean
	 */
	private boolean checkValidApiToken() {
		if (StringUtils.isNullOrEmpty(getLogin()) || StringUtils.isNullOrEmpty(getPassword())) {
			return false;
		}
		if (StringUtils.isNullOrEmpty(apiToken) || System.currentTimeMillis() - tokenExpire >= expiresIn) {
			apiToken = getToken();
		}
		return true;
	}

	/**
	 * populate system data from request
	 *
	 * @param statistics the stats are list of Statistics
	 */
	private void populateSystemData(Map<String, String> statistics) {
		for (PolyLensSystemInfoMetric property : PolyLensSystemInfoMetric.values()) {
			statistics.put(property.getName(), getDefaultValueForNullData(systemInformation.getValueByMetricName(property)));
		}
	}

	/**
	 * get Token from Poly Lens
	 *
	 * @return new token
	 */
	private String getToken() {
		String token;
		String client_id = this.getLogin();
		String client_secret = this.getPassword();
		String body = "{\"client_id\":\"" + client_id + "\",\"client_secret\":\"" + client_secret + "\",\"grant_type\":\"" + PolyLensConstant.GRANT_TYPE + "\"}";
		try {
			JsonNode response = doPost(loginHost, body, JsonNode.class);
			if (response.size() == 1) {
				throw new IllegalArgumentException("ClientId and ClientSecret are not correct");
			}
			tokenExpire = System.currentTimeMillis();
			token = response.get(PolyLensConstant.ACCESS_TOKEN).asText();
			expiresIn = (response.get(PolyLensConstant.EXPIRES_IN).asLong() - 1800) * 1000;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return token;
	}

	/**
	 * Get system information of Poly Lens
	 */
	private void retrieveSystemInfo() {
		try {
			JsonNode systemResponse = this.doPost(PolyLensConstant.URI_POLY_LENS, PolyLensProperties.SYSTEM_INFO.getCommand(), JsonNode.class);
			systemInformation = objectMapper.treeToValue(systemResponse.get(PolyLensConstant.DATA), SystemInformation.class);
		} catch (Exception e) {
			throw new ResourceNotReachableException("Error when get system information", e);
		}
	}

	/**
	 * populate detail aggregated device
	 * add aggregated device into aggregated device list
	 */
	public void populateDeviceDetails() {
		//Todo
	}

	/**
	 * check value is null or empty
	 *
	 * @param value input value
	 * @return value after checking
	 */
	private String getDefaultValueForNullData(String value) {
		return StringUtils.isNotNullOrEmpty(value) && !PolyLensConstant.BRACKETS.equals(value) && !PolyLensConstant.NULL.equals(value) ? value : PolyLensConstant.NONE;
	}
}