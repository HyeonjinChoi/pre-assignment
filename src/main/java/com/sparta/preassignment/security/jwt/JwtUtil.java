package com.sparta.preassignment.security.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.sparta.preassignment.user.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtUtil {
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public final String AUTHORITIES_KEY = "auth";
	public static final String BEARER_PREFIX = "Bearer ";

	@Value("${jwt.access.token}")
	private Long accessTokenTime;
	@Value("${jwt.refresh.token}")
	private Long refreshTokenTime;
	@Value("${jwt.secret.key}")
	private String secretKey;

	private Key key;
	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	@PostConstruct
	public void init() {
		byte[] bytes = Base64.getDecoder().decode(secretKey);
		key = Keys.hmacShaKeyFor(bytes);
	}

	public String createAccessToken(User user) {
		Date now = new Date();
		return BEARER_PREFIX + Jwts.builder()
			.setSubject(user.getUsername())
			.claim(AUTHORITIES_KEY, user.getRole())
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + accessTokenTime))
			.signWith(key, signatureAlgorithm)
			.compact();
	}

	public String createRefreshToken() {
		Date now = new Date();
		return Jwts.builder()
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + refreshTokenTime))
			.signWith(key, signatureAlgorithm)
			.compact();
	}

	public void addJwtToHeader(String token, HttpServletResponse response) {
		response.setHeader(AUTHORIZATION_HEADER, token);
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException | UnsupportedJwtException e) {
			throw new RuntimeException("JWT 서명이 유효하지 않습니다.", e);
		} catch (ExpiredJwtException e) {
			throw new RuntimeException("토큰이 만료되었습니다.", e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("JWT 클레임이 비어있습니다.", e);
		}
	}

	public Claims getClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}

	public String getTokenFromRequest(HttpServletRequest req) {
		String bearerToken = req.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(7);
		}
		return null;
	}

	public String substringToken(String token) {
		if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
			return token.substring(BEARER_PREFIX.length());
		}
		throw new NullPointerException("토큰을 찾을 수 없습니다.");
	}
}