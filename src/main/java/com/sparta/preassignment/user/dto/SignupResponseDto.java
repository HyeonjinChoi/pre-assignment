package com.sparta.preassignment.user.dto;

import com.sparta.preassignment.user.entity.User;

import lombok.Getter;

@Getter
public class SignupResponseDto {

	private final String username;
	private final String nickname;
	private final String authority;

	public SignupResponseDto(User user) {
		this.username = user.getUsername();
		this.nickname = user.getNickname();
		this.authority = user.getRole().getAuthority();
	}
}