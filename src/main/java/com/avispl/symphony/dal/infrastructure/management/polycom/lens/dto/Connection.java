/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.polycom.lens.dto;

/**
 * Connection contains device name, MacAddress and SoftwareVersion
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 4/21/2023
 * @since 1.0.0
 */
public class Connection {
	private String name;
	private String macAddress;
	private String softwareVersion;

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets {@code name}
	 *
	 * @param name the {@code java.lang.String} field
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@code {@link #macAddress}}
	 *
	 * @return value of {@link #macAddress}
	 */
	public String getMacAddress() {
		return macAddress;
	}

	/**
	 * Sets {@code macAddress}
	 *
	 * @param macAddress the {@code java.lang.String} field
	 */
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	/**
	 * Retrieves {@code {@link #softwareVersion}}
	 *
	 * @return value of {@link #softwareVersion}
	 */
	public String getSoftwareVersion() {
		return softwareVersion;
	}

	/**
	 * Sets {@code softwareVersion}
	 *
	 * @param softwareVersion the {@code java.lang.String} field
	 */
	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}
}
