package com.sparta.preassignment.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sparta.preassignment.user.dto.SignupRequestDto;
import com.sparta.preassignment.user.dto.SignupResponseDto;
import com.sparta.preassignment.user.entity.User;
import com.sparta.preassignment.user.entity.UserRoleEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public SignupResponseDto signup(SignupRequestDto signupRequestDto) {
		if (userRepository.existsByUsername(signupRequestDto.getUsername())) {
			throw new IllegalArgumentException("이미 존재하는 이름입니다.");
		}

		if (userRepository.existsByNickname(signupRequestDto.getNickname())) {
			throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
		}

		User user = User.builder()
			.username(signupRequestDto.getUsername())
			.password(passwordEncoder.encode(signupRequestDto.getPassword()))
			.nickname(signupRequestDto.getNickname())
			.role(UserRoleEnum.USER)
			.build();

		userRepository.save(user);

		return new SignupResponseDto(user);
	}
}