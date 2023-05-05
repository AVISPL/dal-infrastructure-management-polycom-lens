/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.polycom.lens.common;

/**
 * PolyLensProperties contain properties name and properties command
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 4/11/2023
 * @since 1.0.0
 */
public enum PolyLensProperties {
	SYSTEM_INFO(PolyLensConstant.SYSTEM_INFO, PolyLensQueries.SYSTEM_INFO, false),
	;
	private final String name;
	private final String command;
	private boolean isControl;

	/**
	 * SennheiserPropertiesList instantiation
	 *
	 * @param name property name
	 * @param command property command
	 * @param isControl controlling or monitoring
	 */
	PolyLensProperties(String name, String command, boolean isControl) {
		this.name = name;
		this.command = command;
		this.isControl = isControl;
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
	 * Retrieves {@code {@link #command}}
	 *
	 * @return value of {@link #command}
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Retrieves {@code {@link #isControl}}
	 *
	 * @return value of {@link #isControl}
	 */
	public boolean isControl() {
		return isControl;
	}
}
