package com.sparta.preassignment.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignResponseDto {
	private String accessToken;
	private String refreshToken;
}
