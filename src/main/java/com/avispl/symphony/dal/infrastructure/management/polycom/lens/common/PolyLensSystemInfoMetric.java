/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.polycom.lens.common;

/**
 * PolyLensSystemInfoMetric contains properties for monitoring aggregator.
 * Represents a system information metric for PolyLens.
 * This class contains the name of the metric and whether it is for a tenant or not.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 4/28/2023
 * @since 1.0.0
 */
public enum PolyLensSystemInfoMetric {
	COUNT_DEVICES(PolyLensConstant.COUNT_DEVICES, false),
	TENANT_COUNT(PolyLensConstant.TENANT_COUNT, false),
	QUERY_COST(PolyLensConstant.QUERY_COST, false),
	COST_USED(PolyLensConstant.COST_USED, false),
	COST_REMAINING(PolyLensConstant.COST_REMAINING, false),
	SECOND_TO_RESET(PolyLensConstant.SECOND_TO_RESET, false),
	TENANT_ID(PolyLensConstant.TENANT_ID, true),
	TENANT_NAME(PolyLensConstant.TENANT_NAME, true),
	TENANT_TYPE(PolyLensConstant.TENANT_TYPE, true),
	TENANT_MEMBER_COUNT(PolyLensConstant.TENANT_MEMBER_COUNT, true),
	;
	private final String name;
	private final boolean isTenant;

	/**
	 * Creates a new instance of the {@code PolyLensSystemInfoMetric} class with the specified name and tenant flag.
	 *
	 * @param name the name of the metric
	 * @param isTenant a flag indicating whether the metric is for a tenant
	 */
	PolyLensSystemInfoMetric(String name, boolean isTenant) {
		this.name = name;
		this.isTenant = isTenant;
	}

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@code {@link #isTenant}}
	 *
	 * @return value of {@link #isTenant}
	 */
	public boolean getTenant() {
		return isTenant;
	}
}
