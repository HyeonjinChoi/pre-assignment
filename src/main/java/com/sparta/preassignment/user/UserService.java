package com.sparta.preassignment.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.preassignment.security.jwt.JwtUtil;
import com.sparta.preassignment.user.dto.SignRequestDto;
import com.sparta.preassignment.user.dto.SignResponseDto;
import com.sparta.preassignment.user.dto.SignupRequestDto;
import com.sparta.preassignment.user.dto.SignupResponseDto;
import com.sparta.preassignment.user.entity.User;
import com.sparta.preassignment.user.entity.UserRoleEnum;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Transactional
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

	@Transactional
	public SignResponseDto login(SignRequestDto signRequestDto) {
		User user = userRepository.findByUsername(signRequestDto.getUsername())
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		if (!passwordEncoder.matches(signRequestDto.getPassword(), user.getPassword())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}

		String accessToken = jwtUtil.createAccessToken(user);
		String refreshToken = jwtUtil.createRefreshToken();

		user.updateRefreshToken(refreshToken);

		return new SignResponseDto(accessToken, refreshToken);
	}

	@Transactional
	public SignResponseDto refreshTokens(String refreshToken) {
		if (!jwtUtil.validateToken(refreshToken)) {
			throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
		}

		Claims claims = jwtUtil.getClaims(refreshToken);
		String username = claims.getSubject();

		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		if (!user.getRefreshToken().equals(refreshToken)) {
			throw new IllegalArgumentException("리프레시 토큰이 일치하지 않습니다.");
		}

		String newAccessToken = jwtUtil.createAccessToken(user);
		String newRefreshToken = jwtUtil.createRefreshToken();

		user.updateRefreshToken(newRefreshToken);

		return new SignResponseDto(newAccessToken, newRefreshToken);
	}
}