/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.polycom.lens.dto.system;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.avispl.symphony.dal.infrastructure.management.polycom.lens.common.PolyLensConstant;
import com.avispl.symphony.dal.infrastructure.management.polycom.lens.common.PolyLensSystemInfoMetric;

/**
 * SystemInformation includes information of aggregator device
 * SystemInformation contains countDevices, gatewayId, myIP, tenantCount, queryCost, tenants
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 4/12/2023
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemInformation {
	private Integer countDevices;
	private Integer tenantCount;

	@JsonProperty("calculateQueryCost")
	private QueryCost queryCost;
	private List<Tenant> tenants;

	/**
	 * Constructs a new SystemInformation object.
	 */
	public SystemInformation() {
	}

	/**
	 * Retrieves {@link #countDevices}
	 *
	 * @return value of {@link #countDevices}
	 */
	public Integer getCountDevices() {
		return countDevices;
	}

	/**
	 * Sets {@link #countDevices} value
	 *
	 * @param countDevices new value of {@link #countDevices}
	 */
	public void setCountDevices(Integer countDevices) {
		this.countDevices = countDevices;
	}

	/**
	 * Retrieves {@link #tenantCount}
	 *
	 * @return value of {@link #tenantCount}
	 */
	public Integer getTenantCount() {
		return tenantCount;
	}

	/**
	 * Sets {@link #tenantCount} value
	 *
	 * @param tenantCount new value of {@link #tenantCount}
	 */
	public void setTenantCount(Integer tenantCount) {
		this.tenantCount = tenantCount;
	}

	/**
	 * Retrieves {@link #queryCost}
	 *
	 * @return value of {@link #queryCost}
	 */
	public QueryCost getQueryCost() {
		return queryCost;
	}

	/**
	 * Sets {@link #queryCost} value
	 *
	 * @param queryCost new value of {@link #queryCost}
	 */
	public void setQueryCost(QueryCost queryCost) {
		this.queryCost = queryCost;
	}

	/**
	 * Retrieves {@link #tenants}
	 *
	 * @return value of {@link #tenants}
	 */
	public List<Tenant> getTenants() {
		return tenants;
	}

	/**
	 * Sets {@link #tenants} value
	 *
	 * @param tenants new value of {@link #tenants}
	 */
	public void setTenants(List<Tenant> tenants) {
		this.tenants = tenants;
	}

	/**
	 * Returns the value associated with the given PolyLensSystemInfoMetric.
	 *
	 * @param name The PolyLensSystemInfoMetric indicating the desired metric value.
	 * @return The value of the specified metric as a String.
	 */
	public String getValueByMetricName(PolyLensSystemInfoMetric name) {
		String result = PolyLensConstant.NONE;
		if (this.getQueryCost() == null) {
			this.setQueryCost(new QueryCost());
		}

		if (this.getTenants() == null || this.getTenants().isEmpty()) {
			List<Tenant> tenants = new ArrayList<>();
			tenants.add(new Tenant());
			this.setTenants(tenants);
		}

		switch (name) {
			case COUNT_DEVICES:
				result = String.valueOf(this.getCountDevices());
				break;
			case TENANT_COUNT:
				result = String.valueOf(this.getTenantCount());
				break;
			case QUERY_COST:
				result = String.valueOf(this.getQueryCost().getQueryCost());
				break;
			case COST_USED:
				result = String.valueOf(this.getQueryCost().getCostUsed());
				break;
			case COST_REMAINING:
				result = String.valueOf(this.getQueryCost().getCostRemaining());
				break;
			case SECOND_TO_RESET:
				result = String.valueOf(this.getQueryCost().getSecondsToReset());
				break;
			case TENANT_ID:
				result = String.valueOf(this.getTenants().get(0).getId());
				break;
			case TENANT_NAME:
				result = String.valueOf(this.getTenants().get(0).getName());
				break;
			case TENANT_TYPE:
				result = String.valueOf(this.getTenants().get(0).getType());
				break;
			case TENANT_MEMBER_COUNT:
				result = String.valueOf(this.getTenants().get(0).getMemberCount());
				break;
		}
		return result;
	}
}

