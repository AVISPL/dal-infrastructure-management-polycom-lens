/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.polycom.lens.common;

/**
 * PolyLensRequest contain the queries
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 4/11/2023
 * @since 1.0.0
 */
public class PolyLensQueries {
	public static final String SYSTEM_INFO = "{\"query\":\"query getPoly {\\n  countDevices\\n  gatewayId\\n  getMyIp {\\n    ip\\n  }\\n  calculateQueryCost {\\n    queryCost\\n    costUsed\\n    costRemaining\\n    secondsToReset\\n  }\\n  tenantCount\\n  tenants {\\n    id\\n    name\\n    type\\n    memberCount\\n    deviceCount\\n  }\\n}\"}";
}
