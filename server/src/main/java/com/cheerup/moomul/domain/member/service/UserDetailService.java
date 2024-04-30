package com.cheerup.moomul.domain.member.service;

import org.springframework.stereotype.Service;

import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.entity.UserDetailDto;
import com.cheerup.moomul.domain.member.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailService {
	private final UserRepository userRepository;

	public UserDetailDto getUser(Long userId){
		if(userRepository.findById(userId).isPresent())
			return new UserDetailDto(userId);
		return null;
	}
}
