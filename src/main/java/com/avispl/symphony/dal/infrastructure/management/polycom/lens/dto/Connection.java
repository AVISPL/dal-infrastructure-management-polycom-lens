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
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets {@link #name} value
	 *
	 * @param name new value of {@link #name}
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@link #macAddress}
	 *
	 * @return value of {@link #macAddress}
	 */
	public String getMacAddress() {
		return macAddress;
	}

	/**
	 * Sets {@link #macAddress} value
	 *
	 * @param macAddress new value of {@link #macAddress}
	 */
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	/**
	 * Retrieves {@link #softwareVersion}
	 *
	 * @return value of {@link #softwareVersion}
	 */
	public String getSoftwareVersion() {
		return softwareVersion;
	}

	/**
	 * Sets {@link #softwareVersion} value
	 *
	 * @param softwareVersion new value of {@link #softwareVersion}
	 */
	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}
}
