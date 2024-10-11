package com.sparta.preassignment.user;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.preassignment.user.dto.SignRequestDto;
import com.sparta.preassignment.user.dto.SignResponseDto;
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
	public ResponseEntity<SignupResponseDto> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.signup(signupRequestDto));
	}

	@PostMapping("/login")
	public ResponseEntity<SignResponseDto> login(@Valid @RequestBody SignRequestDto signRequestDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.login(signRequestDto));
	}

	@PostMapping("/refresh")
	public ResponseEntity<SignResponseDto> refresh(@RequestBody Map<String, String> request) {
		String refreshToken = request.get("token");
		return ResponseEntity.ok(userService.refreshTokens(refreshToken));
	}
}