package com.cheerup.moomul.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cheerup.moomul.domain.member.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
