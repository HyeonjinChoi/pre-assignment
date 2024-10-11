package com.sparta.preassignment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.sparta.preassignment.security.jwt.JwtAuthorizationFilter;
import com.sparta.preassignment.security.jwt.JwtUtil;
import com.sparta.preassignment.security.user.UserDetailsServiceImpl;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

	private final JwtUtil jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;
	private final AuthenticationConfiguration authenticationConfiguration;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public Filter jwtAuthorizationFilter() {
		return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable);
		http.formLogin(AbstractHttpConfigurer::disable);

		http.sessionManagement(sessionManagement ->
			sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		);

		http.authorizeHttpRequests(authorize ->
			authorize
				.requestMatchers("/api/auth/**").permitAll()
				.requestMatchers("/swagger-ui/**", "/v3/**").permitAll()
				.anyRequest().authenticated()
		);

		http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}