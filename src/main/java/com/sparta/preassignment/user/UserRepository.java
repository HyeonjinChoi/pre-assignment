package com.sparta.preassignment.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.preassignment.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByUsername(String username);
	boolean existsByNickname(String nickname);
}