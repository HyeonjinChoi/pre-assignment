package com.sparta.preassignment.user.entity;

public enum UserRoleEnum {
	USER("ROLE_USER"),
	ADMIN("ROLE_ADMIN");

	private final String authority;

	UserRoleEnum(String authority) {
		this.authority = authority;
	}

	public String getAuthority() {
		return this.authority;
	}
}
