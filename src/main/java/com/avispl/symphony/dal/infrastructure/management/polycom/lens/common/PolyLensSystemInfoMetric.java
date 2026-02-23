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
	TENANT_COUNT(PolyLensConstant.TENANT_COUNT, false),
	QUERY_COST(PolyLensConstant.QUERY_COST, true),
	COST_USED(PolyLensConstant.COST_USED, true),
	COST_REMAINING(PolyLensConstant.COST_REMAINING, true),
	SECOND_TO_RESET(PolyLensConstant.SECOND_TO_RESET, true),
	TENANT_ID(PolyLensConstant.TENANT_ID, false),
	TENANT_NAME(PolyLensConstant.TENANT_NAME, false),
	TENANT_TYPE(PolyLensConstant.TENANT_TYPE, false),
	TENANT_MEMBER_COUNT(PolyLensConstant.TENANT_MEMBER_COUNT, false),
	;
	private final String name;
	private final boolean isQueryCost;

	/**
	 * Creates a new instance of the {@code PolyLensSystemInfoMetric} class with the specified name and tenant flag.
	 *
	 * @param name the name of the metric
	 * @param isQueryCost a flag indicating whether the metric is for a tenant
	 */
	PolyLensSystemInfoMetric(String name, boolean isQueryCost) {
		this.name = name;
		this.isQueryCost = isQueryCost;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@link #isQueryCost}
	 *
	 * @return value of {@link #isQueryCost}
	 */
	public boolean isQueryCost() {
		return isQueryCost;
	}
}
