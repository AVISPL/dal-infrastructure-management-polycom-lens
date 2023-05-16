/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.polycom.lens;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.api.dal.monitor.aggregator.Aggregator;
import com.avispl.symphony.dal.aggregator.parser.AggregatedDeviceProcessor;
import com.avispl.symphony.dal.aggregator.parser.PropertiesMapping;
import com.avispl.symphony.dal.aggregator.parser.PropertiesMappingParser;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.infrastructure.management.polycom.lens.common.PolyLensAggregatedMetric;
import com.avispl.symphony.dal.infrastructure.management.polycom.lens.common.PolyLensConstant;
import com.avispl.symphony.dal.infrastructure.management.polycom.lens.common.PolyLensFilteringMetric;
import com.avispl.symphony.dal.infrastructure.management.polycom.lens.common.PolyLensProperties;
import com.avispl.symphony.dal.infrastructure.management.polycom.lens.common.PolyLensSystemInfoMetric;
import com.avispl.symphony.dal.infrastructure.management.polycom.lens.dto.Connection;
import com.avispl.symphony.dal.infrastructure.management.polycom.lens.dto.Entitlement;
import com.avispl.symphony.dal.infrastructure.management.polycom.lens.dto.system.SystemInformation;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * Poly Lens Communicator
 * Supported features are:
 * Monitoring Aggregated Device:
 * <ul>
 * <li> - Id</li>
 * <li> - supportsSettings</li>
 * <li> - supportsSoftwareUpdate</li>
 * <li> - callStatus</li>
 * <li> - tags</li>
 * <li> - etag</li>
 * <li> - connections</li>
 * <li> - entityConnections</li>
 * <li> - user</li>
 * <li> - name</li>
 * <li> - tenantId</li>
 * <li> - productId</li>
 * <li> - organization</li>
 * <li> - site</li>
 * <li> - room</li>
 * <li> - hardwareFamily</li>
 * <li> - hardwareModel</li>
 * <li> - hardwareRevision</li>
 * <li> - softwareVersion</li>
 * <li> - softwareBuild</li>
 * <li> - externalIp</li>
 * <li> - internalIp</li>
 * <li> - macAddress</li>
 * <li> - serialNumber</li>
 * <li> - location</li>
 * <li> - connected</li>
 * <li> - tenant</li>
 * <li> - product</li>
 * <li> - model</li>
 * <li> - availableOSs</li>
 * <li> - currentOSState</li>
 * <li> - activeApplicationVersion</li>
 * <li> - provisioningEnabled</li>
 * <li> - lastConfigRequestDate</li>
 * <li> - lastDetected</li>
 * <li> - shipmentDate</li>
 * <li> - hardwareProduct</li>
 * <li> - proxyAgent</li>
 * <li> - proxyAgentId</li>
 * <li> - proxyAgentVersion</li>
 * <li> - usbVendorId</li>
 * <li> - usbProductId</li>
 * <li> - bandwidth</li>
 * <li> - dateRegistered</li>
 * <li> - dateRegistered</li>
 * <li> - hasPeripherals</li>
 * <li> - allPeripheralsLinked</li>
 * <li> - inVirtualDevice</li>
 * <li> - entitlements</li>
 * <li> - shipment</li>
 * <li> - systemStatus</li>
 * </ul>
 *
 * Controlling:
 * <ul>
 * <li> - rebootDevice</li>
 * </ul>
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 4/11/2023
 * @since 1.0.0
 */
public class PolyLensCommunicator extends RestCommunicator implements Aggregator, Monitorable, Controller {
	/**
	 * Process that is running constantly and triggers collecting data from PoLy Lens API endpoints, based on the given timeouts and thresholds.
	 *
	 * @author Harry
	 * @since 1.0.0
	 */
	class PolyLensDataLoader implements Runnable {
		private volatile boolean inProgress;
		private volatile int threadIndex = 0;
		private volatile int pollingIntervalValue = 1;

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
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
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
					if (PolyLensConstant.NULL.equals(nextToken)) {
						try {
							if (StringUtils.isNotNullOrEmpty(pollingInterval)) {
								pollingIntervalValue = Integer.parseInt(pollingInterval);
							}
							nextDevicesCollectionIterationTimestamp = System.currentTimeMillis() + pollingIntervalValue * 60 * 1000;
						} catch (Exception e) {
							throw new IllegalArgumentException(String.format("Unexpected pollingInterval value: %s", pollingInterval));
						}
					} else {
						nextDevicesCollectionIterationTimestamp = System.currentTimeMillis() + 60000;
					}
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

	/**
	 * filter by room field
	 */
	private String filterRoomName;

	/**
	 * filter by site field
	 */
	private String filterSiteName;

	/**
	 * filter by model field
	 */
	private String filterModelName;

	/**
	 * filter logic NOT by room field
	 */
	private String filterRoomNameNotIn;

	/**
	 * Retrieves the polling interval.
	 * This method returns the value of the polling interval, which represents the time interval between each polling action.
	 */
	private String pollingInterval;

	/**
	 * number of devices obtained in 1 request
	 */
	private int pageSize = PolyLensConstant.NUMBER_OF_DEVICES;

	/**
	 * A private final ReentrantLock instance used to provide exclusive access to a shared resource
	 * that can be accessed by multiple threads concurrently. This lock allows multiple reentrant
	 * locks on the same shared resource by the same thread.
	 */
	private final ReentrantLock reentrantLock = new ReentrantLock();

	/**
	 * A class-level constant instance of JsonNodeFactory, which is a factory class for creating JsonNode instances.
	 * This instance provides a default configuration of the factory with which to create new nodes.
	 */
	static JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;

	/**
	 * A mapper for reading and writing JSON using Jackson library.
	 * ObjectMapper provides functionality for converting between Java objects and JSON.
	 * It can be used to serialize objects to JSON format, and deserialize JSON data to objects.
	 */
	ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * An instance of the AggregatedDeviceProcessor class used to process and aggregate device-related data.
	 */
	private AggregatedDeviceProcessor aggregatedDeviceProcessor;

	/**
	 * A private field that represents an instance of the PolyLensDataLoader class, which is responsible for loading device data for PolyLens.
	 */
	private PolyLensDataLoader deviceDataLoader;

	/**
	 * save time get token
	 */
	private Long tokenExpire;

	/**
	 * time the token expires
	 */
	private Long expiresIn = 84600L * 1000;

	/**
	 * save nextToken for next request
	 */
	private String nextToken = PolyLensConstant.NULL;

	/**
	 * Number of threads used in a poly internal
	 */
	private int threadCount;

	/**
	 * Poly Lens API Token
	 */
	private String apiToken;

	/**
	 * This parameter holds timestamp of when we need to stop performing API calls
	 * It used when device stop retrieving statistic. Updated each time of called #retrieveMultipleStatistics
	 */
	private volatile long validRetrieveStatisticsTimestamp;

	/**
	 * We don't want the statistics to be collected constantly, because if there's not a big list of devices -
	 * new devices' statistics loop will be launched before the next monitoring iteration. To avoid that -
	 * this variable stores a timestamp which validates it, so when the devices' statistics is done collecting, variable
	 * is set to currentTime + 30s, at the same time, calling {@link #retrieveMultipleStatistics()} and updating the
	 * {@link #aggregatedDeviceList} resets it to the currentTime timestamp, which will re-activate data collection.
	 */
	private long nextDevicesCollectionIterationTimestamp;

	/**
	 * Aggregator inactivity timeout. If the {@link PolyLensCommunicator#retrieveMultipleStatistics()}  method is not
	 * called during this period of time - device is considered to be paused, thus the Cloud API
	 * is not supposed to be called
	 */
	private static final long retrieveStatisticsTimeOut = 3 * 60 * 1000;

	/**
	 * Indicates whether a device is considered as paused.
	 * True by default so if the system is rebooted and the actual value is lost -> the device won't start stats
	 * collection unless the {@link PolyLensCommunicator#retrieveMultipleStatistics()} method is called which will change it
	 * to a correct value
	 */
	private volatile boolean devicePaused = true;

	/**
	 * Executor that runs all the async operations, that is posting and
	 */
	private ExecutorService executorService;

	/**
	 * SSL certificate
	 */
	private SSLContext sslContext;

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
	 * List of aggregated device
	 */
	private List<AggregatedDevice> aggregatedDeviceList = Collections.synchronizedList(new ArrayList<>());

	/**
	 * List of System Response
	 */
	private SystemInformation systemInformation = new SystemInformation();

	/**
	 * Retrieves {@link #filterRoomName}
	 *
	 * @return value of {@link #filterRoomName}
	 */
	public String getFilterRoomName() {
		return filterRoomName;
	}

	/**
	 * Sets {@link #filterRoomName} value
	 *
	 * @param filterRoomName new value of {@link #filterRoomName}
	 */
	public void setFilterRoomName(String filterRoomName) {
		this.filterRoomName = filterRoomName;
	}

	/**
	 * Retrieves {@link #filterSiteName}
	 *
	 * @return value of {@link #filterSiteName}
	 */
	public String getFilterSiteName() {
		return filterSiteName;
	}

	/**
	 * Sets {@link #filterSiteName} value
	 *
	 * @param filterSiteName new value of {@link #filterSiteName}
	 */
	public void setFilterSiteName(String filterSiteName) {
		this.filterSiteName = filterSiteName;
	}

	/**
	 * Retrieves {@link #filterModelName}
	 *
	 * @return value of {@link #filterModelName}
	 */
	public String getFilterModelName() {
		return filterModelName;
	}

	/**
	 * Sets {@link #filterModelName} value
	 *
	 * @param filterModelName new value of {@link #filterModelName}
	 */
	public void setFilterModelName(String filterModelName) {
		this.filterModelName = filterModelName;
	}

	/**
	 * Retrieves {@link #filterRoomNameNotIn}
	 *
	 * @return value of {@link #filterRoomNameNotIn}
	 */
	public String getFilterRoomNameNotIn() {
		return filterRoomNameNotIn;
	}

	/**
	 * Sets {@link #filterRoomNameNotIn} value
	 *
	 * @param filterRoomNameNotIn new value of {@link #filterRoomNameNotIn}
	 */
	public void setFilterRoomNameNotIn(String filterRoomNameNotIn) {
		this.filterRoomNameNotIn = filterRoomNameNotIn;
	}

	/**
	 * Retrieves {@link #pageSize}
	 *
	 * @return value of {@link #pageSize}
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Sets {@link #pageSize} value
	 *
	 * @param pageSize new value of {@link #pageSize}
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * Build instance of Poly LensReflectCommunicator
	 * Setup aggregated devices processor
	 *
	 * @throws IOException if unable to locate mapping ymp file or properties file
	 */
	public PolyLensCommunicator() throws IOException {
		Map<String, PropertiesMapping> mapping = new PropertiesMappingParser().loadYML(PolyLensConstant.MODEL_MAPPING_AGGREGATED_DEVICE, getClass());
		aggregatedDeviceProcessor = new AggregatedDeviceProcessor(mapping);
		this.setTrustAllCertificates(true);
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
		if (systemInformation.getCountDevices() != null) {
			threadCount = PolyLensConstant.ONE_THREAD;
			if (systemInformation.getCountDevices() > pageSize) {
				threadCount = PolyLensConstant.TWO_THREADS;
			}
			if (checkValidApiToken()) {
				if (executorService == null) {
					executorService = Executors.newFixedThreadPool(1);
					executorService.submit(deviceDataLoader = new PolyLensDataLoader());
				}
				nextDevicesCollectionIterationTimestamp = System.currentTimeMillis();
				updateValidRetrieveStatisticsTimestamp();
			}
			if (aggregatedDeviceList.isEmpty()) {
				return aggregatedDeviceList;
			}
			return cloneAggregatedDeviceList();
		}
		return aggregatedDeviceList;
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
	public void controlProperty(ControllableProperty controllableProperty) {
		reentrantLock.lock();
		try {
			String property = controllableProperty.getProperty();
			String deviceId = controllableProperty.getDeviceId();
			PolyLensProperties propertyItem = PolyLensProperties.getByName(property);
			switch (propertyItem) {
				case REBOOT_DEVICE:
					sendRequestToControlDevice(propertyItem, deviceId);
					break;
				default:
					logger.debug(String.format("Property name %s doesn't support", propertyItem));
			}
		} finally {
			reentrantLock.unlock();
		}
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
	 * <p>
	 *
	 * Check for available devices before retrieving the value
	 * ping latency information to Symphony
	 */
	@Override
	public int ping() throws Exception {
		if (isInitialized()) {
			long pingResultTotal = 0L;

			for (int i = 0; i < this.getPingAttempts(); i++) {
				long startTime = System.currentTimeMillis();

				try (Socket puSocketConnection = new Socket(this.host, this.getPort())) {
					puSocketConnection.setSoTimeout(this.getPingTimeout());
					if (puSocketConnection.isConnected()) {
						long pingResult = System.currentTimeMillis() - startTime;
						pingResultTotal += pingResult;
						if (this.logger.isTraceEnabled()) {
							this.logger.trace(String.format("PING OK: Attempt #%s to connect to %s on port %s succeeded in %s ms", i + 1, host, this.getPort(), pingResult));
						}
					} else {
						if (this.logger.isDebugEnabled()) {
							this.logger.debug(String.format("PING DISCONNECTED: Connection to %s did not succeed within the timeout period of %sms", host, this.getPingTimeout()));
						}
						return this.getPingTimeout();
					}
				} catch (SocketTimeoutException | ConnectException tex) {
					if (this.logger.isDebugEnabled()) {
						this.logger.error(String.format("PING TIMEOUT: Connection to %s did not succeed within the timeout period of %sms", host, this.getPingTimeout()));
					}
					throw new SocketTimeoutException("Connection timed out");
				} catch (Exception e) {
					if (this.logger.isDebugEnabled()) {
						this.logger.error(String.format("PING TIMEOUT: Connection to %s did not succeed, UNKNOWN ERROR %s: ", host, e.getMessage()));
					}
					return this.getPingTimeout();
				}
			}
			return Math.max(1, Math.toIntExact(pingResultTotal / this.getPingAttempts()));
		} else {
			throw new IllegalStateException("Cannot use device class without calling init() first");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void authenticate() {
		// Poly lens only require API token for each request.
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

		// Create a trust manager that trusts all certificates
		TrustManager[] trustAllCerts = new TrustManager[] {
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return new java.security.cert.X509Certificate[] {};
					}

					public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
					}

					public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
					}
				}
		};

		// Install the all-trusting trust manager
		this.sslContext = SSLContext.getInstance(PolyLensConstant.SSL);
		this.sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
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
		String body = "{\"client_id\":\"" + this.getLogin() + "\",\"client_secret\":\"" + this.getPassword() + "\",\"grant_type\":\"" + PolyLensConstant.GRANT_TYPE + "\"}";
		try {
			JsonNode response = doPost(PolyLensConstant.URL_GET_TOKEN, body, JsonNode.class);
			if (response.size() == 1) {
				throw new IllegalArgumentException("ClientId and ClientSecret are not correct");
			}
			tokenExpire = System.currentTimeMillis();
			token = response.get(PolyLensConstant.ACCESS_TOKEN).asText();
			expiresIn = (response.get(PolyLensConstant.EXPIRES_IN).asLong() - 1800) * 1000;
		} catch (Exception e) {
			throw new ResourceNotReachableException("Can't get token from client id and client secret", e);
		}
		return token;
	}

	/**
	 * Get system information of Poly Lens
	 */
	private void retrieveSystemInfo() {
		try {
			JsonNode systemResponse = this.doPost(PolyLensConstant.URI_POLY_LENS, PolyLensProperties.SYSTEM_INFO.getCommand(), JsonNode.class);
			if (systemResponse == null) {
				throw new ResourceNotReachableException("Error when get system information");
			}
			systemInformation = objectMapper.treeToValue(systemResponse.get(PolyLensConstant.DATA), SystemInformation.class);
		} catch (Exception e) {
			throw new ResourceNotReachableException("Error when get system information", e);
		}
	}

	/**
	 * populate detail aggregated device
	 * add aggregated device into aggregated device list
	 */
	private void populateDeviceDetails() {
		try {
			String query = PolyLensProperties.AGGREGATED_DEVICES.getCommand();
			query = query.replace(PolyLensConstant.VARIABLES, createVariableForFiltering());
			if (!PolyLensConstant.NULL.equals(nextToken)) {
				query = query.replace(PolyLensConstant.NULL, PolyLensConstant.QUOTES + nextToken + PolyLensConstant.QUOTES);
			}
			JsonNode aggregatedDevice = this.doPost(PolyLensConstant.URI_POLY_LENS, query, JsonNode.class);
			JsonNode jsonArray = aggregatedDevice.get(PolyLensConstant.DATA).get(PolyLensConstant.DEVICE_SEARCH).get(PolyLensConstant.EDGES);
			nextToken = aggregatedDevice.get(PolyLensConstant.DATA).get(PolyLensConstant.DEVICE_SEARCH).get(PolyLensConstant.PAGE_INFO).get(PolyLensConstant.NEXT_TOKEN).asText();
			for (JsonNode jsonNode : jsonArray) {
				JsonNode node = objectMapper.createArrayNode().add(jsonNode.get(PolyLensConstant.NODE));

				String id = jsonNode.get(PolyLensConstant.NODE).get(PolyLensConstant.ID).asText();
				aggregatedDeviceList.removeIf(item -> item.getDeviceId().equals(id));
				aggregatedDeviceList.addAll(aggregatedDeviceProcessor.extractDevices(node));
			}
		} catch (Exception e) {
			logger.error("Error while populate aggregated device", e);
		}
	}

	/**
	 * Clone an aggregated device list that based on aggregatedDeviceList variable
	 * populate monitoring and controlling for aggregated device
	 *
	 * @return List<AggregatedDevice> aggregated device list
	 */
	private List<AggregatedDevice> cloneAggregatedDeviceList() {
		List<AggregatedDevice> resultAggregatedDeviceList = new ArrayList<>();
		synchronized (aggregatedDeviceList) {
			for (AggregatedDevice aggregatedDevice : aggregatedDeviceList) {
				AggregatedDevice newClonedAggregatedDevice = new AggregatedDevice();
				newClonedAggregatedDevice.setDeviceId(aggregatedDevice.getDeviceId());
				newClonedAggregatedDevice.setDeviceModel(aggregatedDevice.getDeviceModel());
				newClonedAggregatedDevice.setDeviceName(aggregatedDevice.getDeviceName());
				newClonedAggregatedDevice.setSerialNumber(aggregatedDevice.getSerialNumber());
				newClonedAggregatedDevice.setDeviceOnline(aggregatedDevice.getDeviceOnline());
				newClonedAggregatedDevice.setType(aggregatedDevice.getType());
				newClonedAggregatedDevice.setCategory(aggregatedDevice.getCategory());
				newClonedAggregatedDevice.setDeviceMake(aggregatedDevice.getDeviceMake());

				Map<String, String> oldStats = aggregatedDevice.getProperties();
				List<AdvancedControllableProperty> controllableProperties = aggregatedDevice.getControllableProperties();
				Map<String, String> newProperties;
				newProperties = mapMonitoringProperty(oldStats);
				if (newClonedAggregatedDevice.getDeviceOnline()) {
					createControl(controllableProperties, newProperties);
				}

				newClonedAggregatedDevice.setProperties(newProperties);
				newClonedAggregatedDevice.setControllableProperties(controllableProperties);
				resultAggregatedDeviceList.add(newClonedAggregatedDevice);
			}
		}
		return resultAggregatedDeviceList;
	}

	/**
	 * add control property into controllableProperties
	 *
	 * @param controllableProperties controllableProperty list of aggregated device
	 * @param stats stats of aggregated device
	 */
	private void createControl(List<AdvancedControllableProperty> controllableProperties, Map<String, String> stats) {
		stats.put(PolyLensConstant.REBOOT_DEVICE, PolyLensConstant.EMPTY);
		AdvancedControllableProperty restartButton = createButton(PolyLensConstant.REBOOT_DEVICE, PolyLensConstant.REBOOT, PolyLensConstant.REBOOTING,
				PolyLensConstant.GRACE_PERIOD);
		controllableProperties.add(restartButton);
	}

	/**
	 * map monitoring property into stats of aggregated device
	 *
	 * @param oldStats stats from model mapping
	 * @return stats after modify data
	 */
	private Map<String, String> mapMonitoringProperty(Map<String, String> oldStats) {
		Map<String, String> newProperties = new HashMap<>();
		String connections = oldStats.get(PolyLensConstant.CONNECTIONS);
		String entitlements = oldStats.get(PolyLensConstant.ENTITLEMENTS);
		List<Connection> connectionList;
		List<Entitlement> entitlementList;
		String group;
		for (PolyLensAggregatedMetric property : PolyLensAggregatedMetric.values()) {
			String name = property.getName();
			switch (property) {
				case MODEL:
					newProperties.put(PolyLensConstant.MODEL_GROUP + PolyLensConstant.NAME, getDefaultValueForNullData(oldStats.get(PolyLensConstant.MODEL_NAME)));
					newProperties.put(PolyLensConstant.MODEL_GROUP + PolyLensConstant.DESCRIPTION, getDefaultValueForNullData(oldStats.get(PolyLensConstant.MODEL_DESCRIPTION)));
					newProperties.put(PolyLensConstant.MODEL_GROUP + PolyLensConstant.HARDWARE_FAMILY_NAME, getDefaultValueForNullData(oldStats.get(PolyLensConstant.MODEL_HARDWARE_FAMILY_NAME)));
					newProperties.put(PolyLensConstant.MODEL_GROUP + PolyLensConstant.HARDWARE_MANUFACTURER_NAME, getDefaultValueForNullData(oldStats.get(PolyLensConstant.MODEL_HARDWARE_MANUFACTURER_NAME)));
					break;
				case SYSTEM_STATUS:
					newProperties.put(PolyLensConstant.SYSTEM_STATUS_GROUP + PolyLensConstant.PROVISIONING_STATE,
							uppercaseFirstCharacter(getDefaultValueForNullData(oldStats.get(PolyLensConstant.PROVISIONING_STATE))));
					newProperties.put(PolyLensConstant.SYSTEM_STATUS_GROUP + PolyLensConstant.GLOBAL_DIRECTORY_STATE,
							uppercaseFirstCharacter(getDefaultValueForNullData(oldStats.get(PolyLensConstant.GLOBAL_DIRECTORY_STATE))));
					newProperties.put(PolyLensConstant.SYSTEM_STATUS_GROUP + PolyLensConstant.IP_NETWORK_STATE,
							uppercaseFirstCharacter(getDefaultValueForNullData(oldStats.get(PolyLensConstant.IP_NETWORK_STATE))));
					newProperties.put(PolyLensConstant.SYSTEM_STATUS_GROUP + PolyLensConstant.TRACKABLE_CAMERA_STATE,
							uppercaseFirstCharacter(getDefaultValueForNullData(oldStats.get(PolyLensConstant.TRACKABLE_CAMERA_STATE))));
					newProperties.put(PolyLensConstant.SYSTEM_STATUS_GROUP + PolyLensConstant.CAMERA_STATE, uppercaseFirstCharacter(getDefaultValueForNullData(oldStats.get(PolyLensConstant.CAMERA_STATE))));
					newProperties.put(PolyLensConstant.SYSTEM_STATUS_GROUP + PolyLensConstant.AUDIO_STATE, uppercaseFirstCharacter(getDefaultValueForNullData(oldStats.get(PolyLensConstant.AUDIO_STATE))));
					newProperties.put(PolyLensConstant.SYSTEM_STATUS_GROUP + PolyLensConstant.REMOTE_CONTROL_STATE,
							uppercaseFirstCharacter(getDefaultValueForNullData(oldStats.get(PolyLensConstant.REMOTE_CONTROL_STATE))));
					newProperties.put(PolyLensConstant.SYSTEM_STATUS_GROUP + PolyLensConstant.LOG_THRESHOLD_STATE,
							uppercaseFirstCharacter(getDefaultValueForNullData(oldStats.get(PolyLensConstant.LOG_THRESHOLD_STATE))));
					break;
				case LOCATION:
					newProperties.put(PolyLensConstant.LOCATION_GROUP + PolyLensConstant.LATITUDE, getDefaultValueForNullData(oldStats.get(PolyLensConstant.LOCATION_LATITUDE)));
					newProperties.put(PolyLensConstant.LOCATION_GROUP + PolyLensConstant.LONGITUDE, getDefaultValueForNullData(oldStats.get(PolyLensConstant.LOCATION_LONGITUDE)));
					break;
				case BANDWIDTH:
					newProperties.put(PolyLensConstant.BANDWIDTH_GROUP + PolyLensConstant.END_TIME, convertFormatDateTime(getDefaultValueForNullData(oldStats.get(PolyLensConstant.BANDWIDTH_END_TIME))));
					newProperties.put(PolyLensConstant.BANDWIDTH_GROUP + PolyLensConstant.DOWNLOAD, getDefaultValueForNullData(oldStats.get(PolyLensConstant.BANDWIDTH_DOWNLOAD_MBPS)));
					newProperties.put(PolyLensConstant.BANDWIDTH_GROUP + PolyLensConstant.PING_JITTER, getDefaultValueForNullData(oldStats.get(PolyLensConstant.BANDWIDTH_PING_JITTER_MS)));
					newProperties.put(PolyLensConstant.BANDWIDTH_GROUP + PolyLensConstant.UPLOAD, getDefaultValueForNullData(oldStats.get(PolyLensConstant.BANDWIDTH_UPLOAD_MBPS)));
					newProperties.put(PolyLensConstant.BANDWIDTH_GROUP + PolyLensConstant.LATENCY, getDefaultValueForNullData(oldStats.get(PolyLensConstant.BANDWIDTH_PING_LATENCY_MS)));
					newProperties.put(PolyLensConstant.BANDWIDTH_GROUP + PolyLensConstant.PING_LOSS_PERCENT, getDefaultValueForNullData(oldStats.get(PolyLensConstant.BANDWIDTH_PING_LOSS_PERCENT)));
					break;
				case CONNECTIONS:
					try {
						connectionList = objectMapper.readValue(connections, new TypeReference<List<Connection>>() {
						});
						if (connectionList.isEmpty()) {
							populateNoneDataForConnectionGroup(newProperties);
						} else {
							for (int i = 0; i < connectionList.size(); i++) {
								group = PolyLensConstant.CONNECTION + formatOrderNumber(i, connectionList.size());
								newProperties.put(group + PolyLensConstant.HASH + PolyLensConstant.NAME, getDefaultValueForNullData(connectionList.get(i).getName()));
								newProperties.put(group + PolyLensConstant.HASH + PolyLensConstant.MAC, getDefaultValueForNullData(connectionList.get(i).getMacAddress()));
								newProperties.put(group + PolyLensConstant.HASH + PolyLensConstant.SOFTWARE_VERSION, getDefaultValueForNullData(connectionList.get(i).getSoftwareVersion()));
							}
						}
					} catch (Exception e) {
						populateNoneDataForConnectionGroup(newProperties);
					}
					break;
				case ENTITLEMENTS:
					try {
						entitlementList = objectMapper.readValue(entitlements, new TypeReference<List<Entitlement>>() {
						});
						if (entitlementList.isEmpty()) {
							populateNoneDataForEntitlementsGroup(newProperties);
						} else {
							for (int i = 0; i < entitlementList.size(); i++) {
								group = PolyLensConstant.ENTITLEMENTS + formatOrderNumber(i, entitlementList.size());
								newProperties.put(group + PolyLensConstant.HASH + PolyLensConstant.ENTITLEMENTS_LICENSE_KEY, getDefaultValueForNullData(entitlementList.get(i).getLicenseKey()));
								newProperties.put(group + PolyLensConstant.HASH + PolyLensConstant.ENTITLEMENTS_DATE, convertFormatDateTime(getDefaultValueForNullData(entitlementList.get(i).getDate())));
								newProperties.put(group + PolyLensConstant.HASH + PolyLensConstant.ENTITLEMENTS_EXPIRED, getDefaultValueForNullData(entitlementList.get(i).getExpired()));
								newProperties.put(group + PolyLensConstant.HASH + PolyLensConstant.ENTITLEMENTS_END_DATE, convertFormatDateTime(getDefaultValueForNullData(entitlementList.get(i).getEndDate())));
								newProperties.put(group + PolyLensConstant.HASH + PolyLensConstant.ENTITLEMENTS_PRODUCT_SERIAL, getDefaultValueForNullData(entitlementList.get(i).getProductSerial()));
							}
						}
					} catch (Exception e) {
						populateNoneDataForEntitlementsGroup(newProperties);
					}
					break;
				case ROOM_NAME:
					newProperties.put(name, StringUtils.isNullOrEmpty(oldStats.get(name)) ? PolyLensConstant.NOT_SET : oldStats.get(name));
					break;
				case SITE_NAME:
					newProperties.put(name, StringUtils.isNullOrEmpty(oldStats.get(name)) ? PolyLensConstant.UNKNOWN : oldStats.get(name));
					break;
				case HAS_PERIPHERALS:
				case PROVISIONING_ENABLED:
				case SUPPORTS_SETTINGS:
				case SUPPORTS_SOFTWARE_UPDATE:
				case ALL_PERIPHERALS_LINKS:
					newProperties.put(name, uppercaseFirstCharacter(getDefaultValueForNullData(oldStats.get(name))));
					break;
				case DATE_REGISTERED:
				case LAST_DETECTED:
				case LAST_CONFIG_REQUEST_DATE:
					newProperties.put(name, convertFormatDateTime(getDefaultValueForNullData(oldStats.get(name))));
					break;
				default:
					newProperties.put(name, getDefaultValueForNullData(oldStats.get(name)));
			}
		}
		return newProperties;
	}

	/**
	 * This method is used to format order number base on size of maximumNumber
	 *
	 * @param index index number
	 * @return String is format of the input
	 */
	private String formatOrderNumber(int index, int size) {
		if (size == 1) {
			return PolyLensConstant.EMPTY;
		}
		if (index < 10) {
			return PolyLensConstant.ZERO + (index + 1);
		}
		return String.valueOf(index + 1);
	}

	/**
	 * Populates none data for the connection group in the given stats map.
	 * The connection group includes information about the name, MAC address, and software version.
	 * This method sets the corresponding values in the stats map to "NONE" to indicate that no data is available for these fields.
	 *
	 * @param stats The map containing the connection group statistics.
	 */
	private void populateNoneDataForConnectionGroup(Map<String, String> stats) {
		stats.put(PolyLensConstant.CONNECTIONS_GROUP + PolyLensConstant.NAME, PolyLensConstant.NONE);
		stats.put(PolyLensConstant.CONNECTIONS_GROUP + PolyLensConstant.MAC, PolyLensConstant.NONE);
		stats.put(PolyLensConstant.CONNECTIONS_GROUP + PolyLensConstant.SOFTWARE_VERSION, PolyLensConstant.NONE);
	}

	/**
	 * Populates none data for the entitlements group in the given stats map.
	 * The entitlements group includes information about license keys, dates, expiration status, end dates, and product serial numbers.
	 * This method sets the corresponding values in the stats map to "NONE" to indicate that no data is available for these fields.
	 *
	 * @param stats The map containing the entitlements group statistics.
	 */
	private void populateNoneDataForEntitlementsGroup(Map<String, String> stats) {
		stats.put(PolyLensConstant.ENTITLEMENTS_GROUP + PolyLensConstant.ENTITLEMENTS_LICENSE_KEY, PolyLensConstant.NONE);
		stats.put(PolyLensConstant.ENTITLEMENTS_GROUP + PolyLensConstant.ENTITLEMENTS_DATE, PolyLensConstant.NONE);
		stats.put(PolyLensConstant.ENTITLEMENTS_GROUP + PolyLensConstant.ENTITLEMENTS_EXPIRED, PolyLensConstant.NONE);
		stats.put(PolyLensConstant.ENTITLEMENTS_GROUP + PolyLensConstant.ENTITLEMENTS_END_DATE, PolyLensConstant.NONE);
		stats.put(PolyLensConstant.ENTITLEMENTS_GROUP + PolyLensConstant.ENTITLEMENTS_PRODUCT_SERIAL, PolyLensConstant.NONE);
	}

	/**
	 * send request with param attach in command
	 *
	 * @param propertyItem command
	 * @param deviceId changed value
	 */
	private void sendRequestToControlDevice(PolyLensProperties propertyItem, String deviceId) {
		String command = propertyItem.getCommand().replace(PolyLensConstant.REBOOT_DEVICE_ID, deviceId);
		try {
			JsonNode response = this.doPost(PolyLensConstant.URI_POLY_LENS, command, JsonNode.class);
			JsonNode rebootDevice = response.get(PolyLensConstant.DATA).get(PolyLensConstant.RESTART_DEVICE);
			if (!rebootDevice.get(PolyLensConstant.SUCCESS).asBoolean()) {
				throw new IllegalArgumentException(
						String.format("Can't control property %s. The device has responded with an error: %s", propertyItem.name(), rebootDevice.get(PolyLensConstant.ERROR).asText()));
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Error while reboot the device", e);
		}
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

	/**
	 * Create a button.
	 *
	 * @param name name of the button
	 * @param label label of the button
	 * @param labelPressed label of the button after pressing it
	 * @param gracePeriod grace period of button
	 * @return This returns the instance of {@link AdvancedControllableProperty} type Button.
	 */
	private AdvancedControllableProperty createButton(String name, String label, String labelPressed, long gracePeriod) {
		AdvancedControllableProperty.Button button = new AdvancedControllableProperty.Button();
		button.setLabel(label);
		button.setLabelPressed(labelPressed);
		button.setGracePeriod(gracePeriod);
		return new AdvancedControllableProperty(name, new Date(), button, PolyLensConstant.EMPTY);
	}

	/**
	 * create variable of query GraphQL
	 *
	 * @return variables node
	 */
	private String createVariableForFiltering() {
		ObjectNode filterNode = jsonNodeFactory.objectNode();
		ArrayNode andArr = jsonNodeFactory.arrayNode();
		for (PolyLensFilteringMetric item : PolyLensFilteringMetric.values()) {
			andArr.add(createFilteringNode(getFilterValue(item.getName()), item.getField(), item.getLogic()));
		}
		filterNode.putArray(PolyLensConstant.AND).addAll(andArr);
		ObjectNode paramsNode = jsonNodeFactory.objectNode();
		paramsNode.put(PolyLensConstant.PAGE_SIZE, pageSize);
		paramsNode.set(PolyLensConstant.NEXT_TOKEN, null);
		paramsNode.set(PolyLensConstant.FILTER, filterNode);
		paramsNode.set(PolyLensConstant.SORT, createSortNode());

		ObjectNode variableNode = jsonNodeFactory.objectNode();
		variableNode.set(PolyLensConstant.PARAMS, paramsNode);

		String jsonString = variableNode.toString();
		return PolyLensConstant.VARIABLES_FILTERING + jsonString;
	}

	/**
	 * get filter value by filter name
	 *
	 * @param name name of filter
	 * @return input value of filter
	 */
	private String getFilterValue(String name) {
		switch (name) {
			case PolyLensConstant.FILTER_MODEL:
				return filterModelName;
			case PolyLensConstant.FILTER_ROOM:
				return filterRoomName;
			case PolyLensConstant.FILTER_SITE:
				return filterSiteName;
			case PolyLensConstant.FILTER_NOT_ROOM:
				return filterRoomNameNotIn;
			default:
				return PolyLensConstant.EMPTY;
		}
	}

	/**
	 * Creates an ObjectNode representing the "sort" field in JSON.
	 * This method creates an ObjectNode with the structure:
	 *
	 * @return The created ObjectNode representing the "sort" field.
	 */
	private ObjectNode createSortNode() {
		ObjectNode sortNode = objectMapper.createObjectNode();
		ArrayNode fieldsArrayNode = objectMapper.createArrayNode();

		ObjectNode fieldNode = objectMapper.createObjectNode();
		fieldNode.put(PolyLensConstant.FIELD_NAME, PolyLensConstant.ID);
		fieldNode.put(PolyLensConstant.DIRECTION, PolyLensConstant.ASC);

		fieldsArrayNode.add(fieldNode);

		sortNode.set(PolyLensConstant.FIELDS, fieldsArrayNode);
		return sortNode;
	}

	/**
	 * Creates a JSON node representing a filtering node for GraphQL query.
	 * The filtering node specifies the conditions for filtering data based on the provided input.
	 *
	 * @param input The input string for filtering.
	 * @param name The name of the field to be filtered.
	 * @param logic The logic operator for the filtering node.
	 * @return The JSON node representing the filtering node.
	 */
	private ObjectNode createFilteringNode(String input, String name, String logic) {
		ObjectNode root = jsonNodeFactory.objectNode();
		if (StringUtils.isNullOrEmpty(input)) {
			input = PolyLensConstant.EMPTY;
		}
		List<String> arrayValueFiltering = Arrays.asList(input.split(PolyLensConstant.COMMA));
		if (arrayValueFiltering.isEmpty()) {
			arrayValueFiltering.add(PolyLensConstant.EMPTY);
		}
		ArrayNode logicNode = jsonNodeFactory.arrayNode();

		for (String value : arrayValueFiltering) {
			ObjectNode node = jsonNodeFactory.objectNode();

			if (PolyLensConstant.NOT_SET.equals(value) || PolyLensConstant.UNKNOWN.equals(value)) {
				node.put(PolyLensConstant.EXISTS, false);
			} else {
				node.put(PolyLensConstant.EQ, value);
			}
			node.put(PolyLensConstant.FIELD, name);
			logicNode.add(node);
		}
		root.putArray(logic).addAll(logicNode);

		return root;
	}

	/**
	 * capitalize the first character of the string
	 *
	 * @param input input string
	 * @return string after fix
	 */
	private String uppercaseFirstCharacter(String input) {
		char firstChar = input.charAt(0);
		return Character.toUpperCase(firstChar) + input.substring(1);
	}

	/**
	 * convert default date time to correct format date time
	 *
	 * @param dateTime default date time
	 * @return correct format date time
	 */
	private String convertFormatDateTime(String dateTime) {
		if (PolyLensConstant.NONE.equals(dateTime)) {
			return dateTime;
		}
		String outputDateTime = PolyLensConstant.NONE;
		SimpleDateFormat inputFormatter = new SimpleDateFormat(PolyLensConstant.DEFAULT_FORMAT_DATETIME, Locale.US);
		inputFormatter.setTimeZone(TimeZone.getTimeZone(PolyLensConstant.UTC));
		try {
			Date date = inputFormatter.parse(dateTime);

			SimpleDateFormat outputFormatter = new SimpleDateFormat(PolyLensConstant.NEW_FORMAT_DATETIME, Locale.US);
			outputDateTime = outputFormatter.format(date);
		} catch (Exception e) {
			logger.debug("Error when convert format datetime");
		}
		return outputDateTime;
	}
}