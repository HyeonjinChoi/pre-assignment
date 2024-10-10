package com.sparta.preassignment.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequestDto {

	@NotBlank(message = "이름은 필수 입력 항목입니다.")
	private String username;

	@NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
	@Pattern(
		regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[~!@#$%^&*]).{8,}$",
		message = "비밀번호는 대소문자, 숫자, 특수문자(~!@#$%^&*)를 포함하여 8자 이상이어야 합니다."
	)
	private String password;

	@NotBlank(message = "닉네임은 필수 입력 항목입니다.")
	private String nickname;

	private String intro;
}