/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.polycom.lens.common;

/**
 * PolyLensAggregatedDeviceMetric contains properties for monitoring aggregated devices
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 4/23/2023
 * @since 1.0.0
 */
public enum PolyLensAggregatedMetric {
	SUPPORTS_SETTINGS(PolyLensConstant.SUPPORTS_SETTINGS),
	SUPPORTS_SOFTWARE_UPDATE(PolyLensConstant.SUPPORTS_SOFTWARE_UPDATE),
	CALL_STATUS(PolyLensConstant.CALL_STATUS),
	TAGS(PolyLensConstant.TAGS),
	ETAG(PolyLensConstant.ETAG),
	TENANT_ID(PolyLensConstant.TENANT_ID),
	PRODUCT_ID(PolyLensConstant.PRODUCT_ID),
	ORGANIZATION(PolyLensConstant.ORGANIZATION),
	MANUFACTURER(PolyLensConstant.MANUFACTURER),
	HARDWARE_FAMILY(PolyLensConstant.HARDWARE_FAMILY),
	HARDWARE_REVISION(PolyLensConstant.HARDWARE_REVISION),
	SOFTWARE_VERSION(PolyLensConstant.SOFTWARE_VERSION),
	SOFTWARE_BUILD(PolyLensConstant.SOFTWARE_BUILD),
	EXTERNAL_IP(PolyLensConstant.EXTERNAL_IP),
	INTERNAL_IP(PolyLensConstant.INTERNAL_IP),
	MAC(PolyLensConstant.MAC),
	ACTIVE_APPLICATION_NAME(PolyLensConstant.ACTIVE_APPLICATION_NAME),
	ACTIVE_APPLICATION_VERSION(PolyLensConstant.ACTIVE_APPLICATION_VERSION),
	PROVISIONING_ENABLED(PolyLensConstant.PROVISIONING_ENABLED),
	LAST_CONFIG_REQUEST_DATE(PolyLensConstant.LAST_CONFIG_REQUEST_DATE),
	LAST_DETECTED(PolyLensConstant.LAST_DETECTED),
	SHIPMENT_DATE(PolyLensConstant.SHIPMENT_DATE),
	HARDWARE_PRODUCT(PolyLensConstant.HARDWARE_PRODUCT),
	PROXY_AGENT(PolyLensConstant.PROXY_AGENT),
	PROXY_AGENT_ID(PolyLensConstant.PROXY_AGENT_ID),
	PROXY_AGENT_VERSION(PolyLensConstant.PROXY_AGENT_VERSION),
	USB_PRODUCT_ID(PolyLensConstant.USB_PRODUCT_ID),
	USB_VENDOR_ID(PolyLensConstant.USB_VENDOR_ID),
	DATE_REGISTERED(PolyLensConstant.DATE_REGISTERED),
	HAS_PERIPHERALS(PolyLensConstant.HAS_PERIPHERALS),
	ALL_PERIPHERALS_LINKS(PolyLensConstant.ALL_PERIPHERALS_LINKS),
	IN_VIRTUAL_DEVICE(PolyLensConstant.IN_VIRTUAL_DEVICE),
	USER_NAME(PolyLensConstant.USER_NAME),
	ROOM_NAME(PolyLensConstant.ROOM_NAME),
	SITE_NAME(PolyLensConstant.SITE_NAME),
	MODEL(PolyLensConstant.MODEL),
	SYSTEM_STATUS(PolyLensConstant.SYSTEM_STATUS),
	LOCATION(PolyLensConstant.LOCATION),
	BANDWIDTH(PolyLensConstant.BANDWIDTH),
	CONNECTIONS(PolyLensConstant.CONNECTIONS),
	ENTITLEMENTS(PolyLensConstant.ENTITLEMENTS),
	;
	private final String name;

	/**
	 * Represents an aggregated metric in the PolyLens system.
	 * This class is used to store the name of the metric.
	 *
	 * @param name the name of the aggregated metric
	 */
	PolyLensAggregatedMetric(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}
}
