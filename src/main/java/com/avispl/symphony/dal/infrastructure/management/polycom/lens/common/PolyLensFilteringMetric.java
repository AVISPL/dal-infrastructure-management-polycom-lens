/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.polycom.lens.common;

/**
 * PolyLensFilteringMetric contains name, field, logic in filter
 * Represents a filtering metric used in the PolyLens application. This metric is defined by a name, a field to filter, and a logical operator.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 5/8/2023
 * @since 1.0.0
 */
public enum PolyLensFilteringMetric {
	SITE(PolyLensConstant.FILTER_SITE, PolyLensConstant.SITE, PolyLensConstant.OR),
	MODEL(PolyLensConstant.FILTER_MODEL, PolyLensConstant.HARDWARE_MODEL, PolyLensConstant.OR),
	ROOM_OR(PolyLensConstant.FILTER_ROOM, PolyLensConstant.ROOM, PolyLensConstant.OR),
	ROOM_NOT(PolyLensConstant.FILTER_NOT_ROOM, PolyLensConstant.ROOM, PolyLensConstant.NOT),
	;
	private final String name;
	private final String field;
	private final String logic;

	/**
	 * Constructs a new PolyLensFilteringMetric with the specified name, field, and logic.
	 *
	 * @param name The name of the filtering metric.
	 * @param field The name of the field to filter.
	 * @param logic The logical operator used for filtering.
	 */
	PolyLensFilteringMetric(String name, String field, String logic) {
		this.name = name;
		this.field = field;
		this.logic = logic;
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
	 * Retrieves {@code {@link #field}}
	 *
	 * @return value of {@link #field}
	 */
	public String getField() {
		return field;
	}

	/**
	 * Retrieves {@code {@link #logic}}
	 *
	 * @return value of {@link #logic}
	 */
	public String getLogic() {
		return logic;
	}
}
