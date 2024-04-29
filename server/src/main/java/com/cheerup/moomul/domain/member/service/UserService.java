package com.cheerup.moomul.domain.member.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cheerup.moomul.domain.member.entity.LoginRequestDto;
import com.cheerup.moomul.domain.member.entity.LoginResponseDto;
import com.cheerup.moomul.domain.member.entity.SignUpDto;
import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.jwt.JwtProvider;
import com.cheerup.moomul.domain.member.repository.UserRepository;
import com.cheerup.moomul.global.response.BaseException;
import com.cheerup.moomul.global.response.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;

	public Void signUp(SignUpDto signUpDto) {
		userRepository.save(
			User.builder()
			.username(signUpDto.username())
			.password(signUpDto.password())
			.nickname(signUpDto.nickname())
			.build());
		return null;
	}

	public LoginResponseDto login(LoginRequestDto loginRequestDto) {
		User user=userRepository.findByUsername(loginRequestDto.username())
			.orElseThrow(()->new BaseException(ErrorCode.NO_USER_ERROR));

		if(!user.getPassword().equals(loginRequestDto.password()))
			throw new BaseException(ErrorCode.WRONG_PASSWORD);

		String accessToken=jwtProvider.createToken(user.getId(),1000L * 60 * 60 * 24 * 7);
		String refreshToken=jwtProvider.createToken(user.getId(),1000L * 60 * 60 * 24 * 7);


		return new LoginResponseDto(
			user.getId(),
			accessToken,
			refreshToken);

	}
	public User getUser(Long userId){
		if(userRepository.findById(userId).isPresent())
			return userRepository.findById(userId).get();
		return null;
	}
}
