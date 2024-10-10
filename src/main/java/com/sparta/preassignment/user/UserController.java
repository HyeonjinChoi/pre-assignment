package com.sparta.preassignment.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.preassignment.user.dto.SignupRequestDto;
import com.sparta.preassignment.user.dto.SignupResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

	private final UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<SignupResponseDto> signup(@Valid SignupRequestDto signupRequestDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.signup(signupRequestDto));
	}
}