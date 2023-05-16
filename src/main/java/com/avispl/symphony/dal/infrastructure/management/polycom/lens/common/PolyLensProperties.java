/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.polycom.lens.common;

import java.util.Arrays;
import java.util.Optional;

/**
 * PolyLensProperties contain properties name and properties command
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 4/11/2023
 * @since 1.0.0
 */
public enum PolyLensProperties {
	SYSTEM_INFO(PolyLensConstant.SYSTEM_INFO, PolyLensQueries.SYSTEM_INFO, false),
	AGGREGATED_DEVICES(PolyLensConstant.AGGREGATED_DEVICES, PolyLensQueries.AGGREGATED_DEVICES, false),
	REBOOT_DEVICE(PolyLensConstant.REBOOT_DEVICE, PolyLensQueries.REBOOT_DEVICE, true),
	;
	private final String name;
	private final String command;
	private boolean isControl;

	/**
	 * Constructs a PolyLensProperties object with the provided parameters.
	 *
	 * @param name The name of the PolyLens property.
	 * @param command The command associated with the PolyLens property.
	 * @param isControl A boolean value indicating whether the PolyLens property is a control property.
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

	/**
	 * Returns the {@link PolyLensProperties} enum constant with the specified name.
	 *
	 * @param name the name of the PolyLensProperties constant to retrieve
	 * @return the PolyLensProperties constant with the specified name
	 * @throws IllegalStateException if no constant with the specified name is found
	 */
	public static PolyLensProperties getByName(String name) {
		Optional<PolyLensProperties> property = Arrays.stream(PolyLensProperties.values()).filter(group -> group.getName().equals(name)).findFirst();
		if (property.isPresent()) {
			return property.get();
		} else {
			throw new IllegalStateException(String.format("control group %s is not supported.", name));
		}
	}
}
