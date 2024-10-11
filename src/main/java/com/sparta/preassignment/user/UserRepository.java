package com.sparta.preassignment.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.preassignment.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);
	boolean existsByUsername(String username);
	boolean existsByNickname(String nickname);
}