/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.polycom.lens;


import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
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
		polyLensCommunicator.setPassword("JS2cyHu2BIBYCiIpmF0qBztM5PxPpA_9zZrHQqaNttdn47Wk");
		polyLensCommunicator.init();
		polyLensCommunicator.connect();
	}

	@AfterEach
	void destroy() throws Exception {
		polyLensCommunicator.disconnect();
		polyLensCommunicator.destroy();
	}

	/**
	 * test get system information
	 */
	@Test
	void testGetAggregatorData() throws Exception {
		extendedStatistic = (ExtendedStatistics) polyLensCommunicator.getMultipleStatistics().get(0);
		Map<String, String> statistics = extendedStatistic.getStatistics();
		Assert.assertEquals(13, statistics.size());
		Assert.assertEquals("1", statistics.get(PolyLensConstant.TENANT_COUNT));
		Assert.assertEquals("AVI-SPL Lab", statistics.get(PolyLensConstant.TENANT_NAME));
		Assert.assertEquals("24", statistics.get(PolyLensConstant.TENANT_MEMBER_COUNT));
		Assert.assertEquals("ENTERPRISE", statistics.get(PolyLensConstant.TENANT_TYPE));
		Assert.assertEquals("b0a59055-8875-4f44-a88a-0b114be771b9", statistics.get(PolyLensConstant.TENANT_ID));
		Assert.assertEquals("42", statistics.get(PolyLensConstant.COUNT_DEVICES));
	}
}