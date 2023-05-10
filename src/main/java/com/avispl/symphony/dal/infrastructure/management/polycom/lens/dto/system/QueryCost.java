/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.polycom.lens.dto.system;

/**
 * Manage query cost in graphQL
 * QueryCost includes queryCost, costUsed, costRemaining, secondsToReset
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 4/28/2023
 * @since 1.0.0
 */
public class QueryCost {
	private Integer queryCost;
	private Integer costUsed;
	private Integer costRemaining;
	private Integer secondsToReset;

	/**
	 * Retrieves {@code {@link #queryCost}}
	 *
	 * @return value of {@link #queryCost}
	 */
	public Integer getQueryCost() {
		return queryCost;
	}

	/**
	 * Sets {@code queryCost}
	 *
	 * @param queryCost the {@code java.lang.Integer} field
	 */
	public void setQueryCost(Integer queryCost) {
		this.queryCost = queryCost;
	}

	/**
	 * Retrieves {@code {@link #costUsed}}
	 *
	 * @return value of {@link #costUsed}
	 */
	public Integer getCostUsed() {
		return costUsed;
	}

	/**
	 * Sets {@code costUsed}
	 *
	 * @param costUsed the {@code java.lang.Integer} field
	 */
	public void setCostUsed(Integer costUsed) {
		this.costUsed = costUsed;
	}

	/**
	 * Retrieves {@code {@link #costRemaining}}
	 *
	 * @return value of {@link #costRemaining}
	 */
	public Integer getCostRemaining() {
		return costRemaining;
	}

	/**
	 * Sets {@code costRemaining}
	 *
	 * @param costRemaining the {@code java.lang.Integer} field
	 */
	public void setCostRemaining(Integer costRemaining) {
		this.costRemaining = costRemaining;
	}

	/**
	 * Retrieves {@code {@link #secondsToReset}}
	 *
	 * @return value of {@link #secondsToReset}
	 */
	public Integer getSecondsToReset() {
		return secondsToReset;
	}

	/**
	 * Sets {@code secondsToReset}
	 *
	 * @param secondsToReset the {@code java.lang.Integer} field
	 */
	public void setSecondsToReset(Integer secondsToReset) {
		this.secondsToReset = secondsToReset;
	}
}
