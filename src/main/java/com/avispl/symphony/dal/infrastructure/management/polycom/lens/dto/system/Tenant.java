/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.polycom.lens.dto.system;

/**
 * TenantDTO contains tenant details information such as : id, name, type, deviceCount, memberCount
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 4/19/2023
 * @since 1.0.0
 */
public class Tenant {
	private String id;
	private String name;
	private String type;
	private Integer memberCount;

	/**
	 * Retrieves {@link #id}
	 *
	 * @return value of {@link #id}
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets {@link #id} value
	 *
	 * @param id new value of {@link #id}
	 */
	public void setId(String id) {
		this.id = id;
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
	 * Sets {@link #name} value
	 *
	 * @param name new value of {@link #name}
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@link #type}
	 *
	 * @return value of {@link #type}
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets {@link #type} value
	 *
	 * @param type new value of {@link #type}
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Retrieves {@link #memberCount}
	 *
	 * @return value of {@link #memberCount}
	 */
	public Integer getMemberCount() {
		return memberCount;
	}

	/**
	 * Sets {@link #memberCount} value
	 *
	 * @param memberCount new value of {@link #memberCount}
	 */
	public void setMemberCount(Integer memberCount) {
		this.memberCount = memberCount;
	}
}
