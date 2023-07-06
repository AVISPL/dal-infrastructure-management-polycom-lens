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
	 * Retrieves {@link #queryCost}
	 *
	 * @return value of {@link #queryCost}
	 */
	public Integer getQueryCost() {
		return queryCost;
	}

	/**
	 * Sets {@link #queryCost} value
	 *
	 * @param queryCost new value of {@link #queryCost}
	 */
	public void setQueryCost(Integer queryCost) {
		this.queryCost = queryCost;
	}

	/**
	 * Retrieves {@link #costUsed}
	 *
	 * @return value of {@link #costUsed}
	 */
	public Integer getCostUsed() {
		return costUsed;
	}

	/**
	 * Sets {@link #costUsed} value
	 *
	 * @param costUsed new value of {@link #costUsed}
	 */
	public void setCostUsed(Integer costUsed) {
		this.costUsed = costUsed;
	}

	/**
	 * Retrieves {@link #costRemaining}
	 *
	 * @return value of {@link #costRemaining}
	 */
	public Integer getCostRemaining() {
		return costRemaining;
	}

	/**
	 * Sets {@link #costRemaining} value
	 *
	 * @param costRemaining new value of {@link #costRemaining}
	 */
	public void setCostRemaining(Integer costRemaining) {
		this.costRemaining = costRemaining;
	}

	/**
	 * Retrieves {@link #secondsToReset}
	 *
	 * @return value of {@link #secondsToReset}
	 */
	public Integer getSecondsToReset() {
		return secondsToReset;
	}

	/**
	 * Sets {@link #secondsToReset} value
	 *
	 * @param secondsToReset new value of {@link #secondsToReset}
	 */
	public void setSecondsToReset(Integer secondsToReset) {
		this.secondsToReset = secondsToReset;
	}
}
