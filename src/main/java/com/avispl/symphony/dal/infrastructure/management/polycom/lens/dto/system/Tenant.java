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
	 * Retrieves {@code {@link #id}}
	 *
	 * @return value of {@link #id}
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets {@code id}
	 *
	 * @param id the {@code java.lang.String} field
	 */
	public void setId(String id) {
		this.id = id;
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
	 * Sets {@code name}
	 *
	 * @param name the {@code java.lang.String} field
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@code {@link #type}}
	 *
	 * @return value of {@link #type}
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets {@code type}
	 *
	 * @param type the {@code java.lang.String} field
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Retrieves {@code {@link #memberCount}}
	 *
	 * @return value of {@link #memberCount}
	 */
	public Integer getMemberCount() {
		return memberCount;
	}

	/**
	 * Sets {@code memberCount}
	 *
	 * @param memberCount the {@code java.lang.Integer} field
	 */
	public void setMemberCount(Integer memberCount) {
		this.memberCount = memberCount;
	}
}
