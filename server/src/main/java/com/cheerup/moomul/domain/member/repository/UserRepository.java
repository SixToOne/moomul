package com.cheerup.moomul.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cheerup.moomul.domain.member.entity.User;

public interface UserRepository extends JpaRepository<User, Long>,UserRepositoryCustom {
	Optional<User> findByUsername(String username);

	Optional<User> findById(Long userId);
}
