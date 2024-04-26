package com.cheerup.moomul.domain.member.service;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cheerup.moomul.domain.member.entity.SignUpDto;
import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public Void signUp(SignUpDto signUpDto) {
		userRepository.save(
			User.builder()
			.username(signUpDto.username())
			.password(signUpDto.password())
			.nickname(signUpDto.nickname())
			.build());
		return null;
	}
}
