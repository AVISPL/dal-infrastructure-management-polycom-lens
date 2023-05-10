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
	 * Retrieves {@code {@link #date}}
	 *
	 * @return value of {@link #date}
	 */
	public String getDate() {
		return date;
	}

	/**
	 * Sets {@code date}
	 *
	 * @param date the {@code java.lang.String} field
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * Retrieves {@code {@link #endDate}}
	 *
	 * @return value of {@link #endDate}
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * Sets {@code endDate}
	 *
	 * @param endDate the {@code java.lang.String} field
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * Retrieves {@code {@link #expired}}
	 *
	 * @return value of {@link #expired}
	 */
	public String getExpired() {
		return expired;
	}

	/**
	 * Sets {@code expired}
	 *
	 * @param expired the {@code java.lang.String} field
	 */
	public void setExpired(String expired) {
		this.expired = expired;
	}

	/**
	 * Retrieves {@code {@link #licenseKey}}
	 *
	 * @return value of {@link #licenseKey}
	 */
	public String getLicenseKey() {
		return licenseKey;
	}

	/**
	 * Sets {@code licenseKey}
	 *
	 * @param licenseKey the {@code java.lang.String} field
	 */
	public void setLicenseKey(String licenseKey) {
		this.licenseKey = licenseKey;
	}

	/**
	 * Retrieves {@code {@link #productSerial}}
	 *
	 * @return value of {@link #productSerial}
	 */
	public String getProductSerial() {
		return productSerial;
	}

	/**
	 * Sets {@code productSerial}
	 *
	 * @param productSerial the {@code java.lang.String} field
	 */
	public void setProductSerial(String productSerial) {
		this.productSerial = productSerial;
	}
}
