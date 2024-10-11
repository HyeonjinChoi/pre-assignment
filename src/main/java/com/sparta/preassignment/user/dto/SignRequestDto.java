package com.sparta.preassignment.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignRequestDto {

	@NotBlank(message = "이름을 입력하세요.")
	private String username;

	@NotBlank(message = "비밀번호를 입력하세요.")
	private String password;
}