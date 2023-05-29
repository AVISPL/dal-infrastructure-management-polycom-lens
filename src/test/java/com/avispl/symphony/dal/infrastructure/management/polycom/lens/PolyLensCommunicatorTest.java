/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.polycom.lens;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.dal.infrastructure.management.polycom.lens.common.PolyLensConstant;

/**
 * Unit test for {@link PolyLensCommunicator}.
 * Test monitoring data with all systems and aggregator device
 *
 * @author Ivan, Harry
 * @version 2.0.0
 * @since 2.0.0
 */
class PolyLensCommunicatorTest {
	private ExtendedStatistics extendedStatistic;
	private PolyLensCommunicator polyLensCommunicator;

	@BeforeEach
	void setUp() throws Exception {
		polyLensCommunicator = new PolyLensCommunicator();
		polyLensCommunicator.setHost("api.silica-prod01.io.lens.poly.com");
		polyLensCommunicator.setPort(443);
		polyLensCommunicator.setLogin("Zcj4Xm5D7DNOlAldItivfE2DIrhyDvDq");
		polyLensCommunicator.setPassword("***REMOVED***");
		polyLensCommunicator.init();
		polyLensCommunicator.connect();
	}

	@AfterEach
	void destroy() throws Exception {
		polyLensCommunicator.disconnect();
		polyLensCommunicator.destroy();
	}

	/**
	 * test get system info of Poly Lens
	 */
	@Test
	void testGetAggregatorData() throws Exception {
		extendedStatistic = (ExtendedStatistics) polyLensCommunicator.getMultipleStatistics().get(0);
		Map<String, String> statistics = extendedStatistic.getStatistics();
		Assert.assertEquals(11, statistics.size());
		Assert.assertEquals("1", statistics.get(PolyLensConstant.TENANT_COUNT));
		Assert.assertEquals("AVI-SPL Lab", statistics.get(PolyLensConstant.TENANT_NAME));
		Assert.assertEquals("24", statistics.get(PolyLensConstant.TENANT_MEMBER_COUNT));
		Assert.assertEquals("ENTERPRISE", statistics.get(PolyLensConstant.TENANT_TYPE));
		Assert.assertEquals("b0a59055-8875-4f44-a88a-0b114be771b9", statistics.get(PolyLensConstant.TENANT_ID));
		Assert.assertEquals("45", statistics.get(PolyLensConstant.COUNT_DEVICES));
		Assert.assertEquals("1", statistics.get(PolyLensConstant.UPDATE_INTERVAL));
	}

	/**
	 * test get system info of Poly Lens
	 */
	@Test
	void testGetAggregatorDataWithFilteringAndNumberDevicePerRequest() throws Exception {
		polyLensCommunicator.setFilterModelName("Lens Desktop,Studio USB");
		extendedStatistic = (ExtendedStatistics) polyLensCommunicator.getMultipleStatistics().get(0);
		Map<String, String> statistics = extendedStatistic.getStatistics();
		Assert.assertEquals(11, statistics.size());
		Assert.assertEquals("1", statistics.get(PolyLensConstant.TENANT_COUNT));
		Assert.assertEquals("AVI-SPL Lab", statistics.get(PolyLensConstant.TENANT_NAME));
		Assert.assertEquals("24", statistics.get(PolyLensConstant.TENANT_MEMBER_COUNT));
		Assert.assertEquals("ENTERPRISE", statistics.get(PolyLensConstant.TENANT_TYPE));
		Assert.assertEquals("b0a59055-8875-4f44-a88a-0b114be771b9", statistics.get(PolyLensConstant.TENANT_ID));
		Assert.assertEquals("13", statistics.get(PolyLensConstant.COUNT_DEVICES));
		Assert.assertEquals("1", statistics.get(PolyLensConstant.UPDATE_INTERVAL));
	}

	/**
	 * test get aggregated device list with one request
	 */
	@Test
	void testGetMultipleStatisticsWithOneRequest() throws Exception {
		polyLensCommunicator.getMultipleStatistics();
		polyLensCommunicator.retrieveMultipleStatistics();
		Thread.sleep(60000);
		List<AggregatedDevice> aggregatedDeviceList = polyLensCommunicator.retrieveMultipleStatistics();
		Assert.assertEquals(45, aggregatedDeviceList.size());
	}

	/**
	 * test get aggregated device list with multi requests
	 */
	@Test
	void testGetMultipleStatisticsWithMultiRequest() throws Exception {
		polyLensCommunicator.setFilterModelName("Lens Desktop,Studio USB");
		polyLensCommunicator.getMultipleStatistics();
		polyLensCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = polyLensCommunicator.retrieveMultipleStatistics();
		Assert.assertEquals(13, aggregatedDeviceList.size());
	}

	/**
	 * test get info of aggregated device
	 */
	@Test
	void testMonitoringForAggregatedDevice() throws Exception {
		polyLensCommunicator.getMultipleStatistics();
		polyLensCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = polyLensCommunicator.retrieveMultipleStatistics();
		Assert.assertEquals(45, aggregatedDeviceList.size());
		AggregatedDevice aggregatedDevice = aggregatedDeviceList.get(0);
		Assert.assertNotNull(aggregatedDevice.getDeviceId());
		Assert.assertNotNull(aggregatedDevice.getDeviceOnline());
		Assert.assertNotNull(aggregatedDevice.getDeviceModel());
		Assert.assertNotNull(aggregatedDevice.getDeviceName());
		Assert.assertNotNull(aggregatedDevice.getSerialNumber());
	}

	/**
	 * test reboot device
	 */
	@Test
	void testRebootDevice() throws Exception {
		polyLensCommunicator.getMultipleStatistics();
		polyLensCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		polyLensCommunicator.retrieveMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = PolyLensConstant.REBOOT_DEVICE;
		String value = "0";
		String deviceId = "00e0db506858";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		controllableProperty.setDeviceId(deviceId);
		polyLensCommunicator.controlProperty(controllableProperty);
	}

	/**
	 * test Filter Room
	 */
	@Test
	void testFiltering() throws Exception {
		polyLensCommunicator.setFilterModelName("Jerry Blayne - Office, CCX 500, PLT Focus,");
		polyLensCommunicator.setFilterRoomName("");
		polyLensCommunicator.setFilterSiteName("USA - OH - Broadview Heights, USA - NY - New York");
		polyLensCommunicator.setFilterExcludeRoomName("");
		polyLensCommunicator.getMultipleStatistics();
		polyLensCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = polyLensCommunicator.retrieveMultipleStatistics();
		Assert.assertEquals(2, aggregatedDeviceList.size());
	}

	/**
	 * test Filter With multi field
	 */
	@Test
	void testFilteringWithMultiField() throws Exception {
		polyLensCommunicator.setFilterModelName("");
		polyLensCommunicator.getMultipleStatistics();
		polyLensCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = polyLensCommunicator.retrieveMultipleStatistics();
		System.out.println(aggregatedDeviceList.size());
		polyLensCommunicator.internalDestroy();
		polyLensCommunicator.internalInit();
		polyLensCommunicator.setFilterModelName("Lens Desktop,Studio USB");
		polyLensCommunicator.setFilterRoomName("");
		polyLensCommunicator.setFilterSiteName(" USA - IL - Chicago, USA-CO-Westminster,USA - OH - Broadview Heights");
		polyLensCommunicator.setFilterExcludeRoomName("Not Set");
		polyLensCommunicator.getMultipleStatistics();
		polyLensCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		aggregatedDeviceList = polyLensCommunicator.retrieveMultipleStatistics();
		Assert.assertEquals(4, aggregatedDeviceList.size());
	}

	/**
	 * test Filter With multi field
	 */
	@Test
	void testFilterNotRoom() throws Exception {
		polyLensCommunicator.setFilterModelName("Lens Desktop,Studio USB");
		polyLensCommunicator.setFilterRoomName("");
		polyLensCommunicator.setFilterSiteName("");
		polyLensCommunicator.setFilterExcludeRoomName("Not Set");
		polyLensCommunicator.getMultipleStatistics();
		polyLensCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = polyLensCommunicator.retrieveMultipleStatistics();
		Assert.assertEquals(7, aggregatedDeviceList.size());
	}

	/**
	 * test Filter With multi field
	 */
	@Test
	void testMultiFiltering() throws Exception {
		polyLensCommunicator.setFilterModelName("Voyager Focus,  Lens Desktop");
		polyLensCommunicator.setFilterRoomName("Home Office - Jerry Blayne,  Lab");
		polyLensCommunicator.setFilterSiteName("USA - OH - Brecksville, USA - PA - Scranton");
		polyLensCommunicator.setFilterExcludeRoomName("Office - Jerry Blayne, USA - OH - Broadview Heights, Lab");
		polyLensCommunicator.getMultipleStatistics();
		polyLensCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = polyLensCommunicator.retrieveMultipleStatistics();
		Assert.assertEquals(2, aggregatedDeviceList.size());
	}
}