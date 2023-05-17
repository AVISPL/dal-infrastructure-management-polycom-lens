/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.polycom.lens.dto;

/**
 * Entitlement contains date, endDate, expired, licenseKey, productSerial
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 4/21/2023
 * @since 1.0.0
 */
public class Entitlement {
	private String date;
	private String endDate;
	private String expired;
	private String licenseKey;
	private String productSerial;

	/**
	 * Retrieves {@link #date}
	 *
	 * @return value of {@link #date}
	 */
	public String getDate() {
		return date;
	}

	/**
	 * Sets {@link #date} value
	 *
	 * @param date new value of {@link #date}
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * Retrieves {@link #endDate}
	 *
	 * @return value of {@link #endDate}
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * Sets {@link #endDate} value
	 *
	 * @param endDate new value of {@link #endDate}
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * Retrieves {@link #expired}
	 *
	 * @return value of {@link #expired}
	 */
	public String getExpired() {
		return expired;
	}

	/**
	 * Sets {@link #expired} value
	 *
	 * @param expired new value of {@link #expired}
	 */
	public void setExpired(String expired) {
		this.expired = expired;
	}

	/**
	 * Retrieves {@link #licenseKey}
	 *
	 * @return value of {@link #licenseKey}
	 */
	public String getLicenseKey() {
		return licenseKey;
	}

	/**
	 * Sets {@link #licenseKey} value
	 *
	 * @param licenseKey new value of {@link #licenseKey}
	 */
	public void setLicenseKey(String licenseKey) {
		this.licenseKey = licenseKey;
	}

	/**
	 * Retrieves {@link #productSerial}
	 *
	 * @return value of {@link #productSerial}
	 */
	public String getProductSerial() {
		return productSerial;
	}

	/**
	 * Sets {@link #productSerial} value
	 *
	 * @param productSerial new value of {@link #productSerial}
	 */
	public void setProductSerial(String productSerial) {
		this.productSerial = productSerial;
	}
}
