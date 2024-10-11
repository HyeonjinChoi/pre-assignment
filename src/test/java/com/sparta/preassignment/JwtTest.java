package com.sparta.preassignment;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.sparta.preassignment.security.jwt.JwtUtil;
import com.sparta.preassignment.user.entity.User;
import com.sparta.preassignment.user.entity.UserRoleEnum;

public class JwtTest {

	private final String secretKey = Base64.getEncoder().encodeToString("my-custom-secret-key-which-is-secure".getBytes());

	@InjectMocks
	private JwtUtil jwtUtil;

	private User testUser;

	@BeforeEach
	void setUp() throws NoSuchFieldException, IllegalAccessException {
		jwtUtil = new JwtUtil();

		setFieldValue(jwtUtil, "secretKey", secretKey);
		Long accessTokenTime = 10000L;
		setFieldValue(jwtUtil, "accessTokenTime", accessTokenTime);
		Long refreshTokenTime = 60000L;
		setFieldValue(jwtUtil, "refreshTokenTime", refreshTokenTime);

		testUser = User.builder()
			.id(1L)
			.username("testuser")
			.password("Password123!!!")
			.nickname("testnick")
			.role(UserRoleEnum.USER)
			.build();
	}

	private void setFieldValue(Object target, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
		Field field = target.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(target, value);
	}

	@Test
	@DisplayName("유효한 사용자 정보로 Access Token 발행")
	void createAccessToken_ShouldReturnValidToken() {
		// given
		User user = testUser;

		// when
		String token = jwtUtil.createAccessToken(user);

		// then
		assertNotNull(token);
		assertTrue(token.startsWith(JwtUtil.BEARER_PREFIX));
	}

	@Test
	@DisplayName("유효한 사용자 정보로 Refresh Token 발행")
	void createRefreshToken_ShouldReturnValidToken() {
		// given

		// when
		String refreshToken = jwtUtil.createRefreshToken();

		// then
		assertNotNull(refreshToken);
	}

	@Test
	@DisplayName("유효한 Access Token의 검증")
	void validateToken_ShouldReturnTrueForValidToken() {
		// given
		String token = jwtUtil.createAccessToken(testUser);
		String tokenValue = jwtUtil.substringToken(token);

		// when
		boolean isValid = jwtUtil.validateToken(tokenValue);

		// then
		assertTrue(isValid);
	}

	@Test
	@DisplayName("잘못된 토큰 검증 시 예외 발생")
	void validateToken_ShouldThrowExceptionForInvalidToken() {
		// given
		String invalidToken = "invalid.token.value";

		// when & then
		assertThrows(RuntimeException.class, () -> jwtUtil.validateToken(invalidToken));
	}

	@Test
	@DisplayName("헤더에서 토큰 추출 테스트")
	void getTokenFromHeader_ShouldReturnTokenWithoutBearerPrefix() {
		// given
		MockHttpServletRequest request = new MockHttpServletRequest();
		String token = "token";
		request.addHeader(JwtUtil.AUTHORIZATION_HEADER, JwtUtil.BEARER_PREFIX + token);

		// when
		String extractedToken = jwtUtil.getTokenFromRequest(request);

		// then
		assertEquals(token, extractedToken);
	}

	@Test
	@DisplayName("헤더에 토큰이 없으면 null 반환")
	void getTokenFromHeader_ShouldReturnNullIfNoTokenPresent() {
		// given
		MockHttpServletRequest request = new MockHttpServletRequest();

		// when
		String extractedToken = jwtUtil.getTokenFromRequest(request);

		// then
		assertNull(extractedToken);
	}

	@Test
	@DisplayName("Access Token을 헤더에 추가")
	void addJwtToHeader_ShouldAddAccessTokenToHeader() {
		// given
		MockHttpServletResponse response = new MockHttpServletResponse();
		String token = "test.token";

		// when
		jwtUtil.addJwtToHeader(JwtUtil.BEARER_PREFIX + token, response);

		// then
		assertEquals(JwtUtil.BEARER_PREFIX + token, response.getHeader(JwtUtil.AUTHORIZATION_HEADER));
	}
}
