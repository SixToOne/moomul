package com.cheerup.moomul.domain.member.service;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.cheerup.moomul.domain.member.entity.AuthResponseDto;
import com.cheerup.moomul.domain.member.entity.LoginRequestDto;
import com.cheerup.moomul.domain.member.entity.LoginResponseDto;
import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.entity.UserDetailDto;
import com.cheerup.moomul.domain.member.jwt.JwtProvider;
import com.cheerup.moomul.domain.member.repository.UserRepository;
import com.cheerup.moomul.global.response.BaseException;
import com.cheerup.moomul.global.response.ErrorCode;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailService {
	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;

	public UserDetailDto getUser(Long userId){
		if(userRepository.findById(userId).isPresent())
			return new UserDetailDto(userId);
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

	public AuthResponseDto auth(HttpServletRequest request) {
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		if(authorizationHeader == null) {
			throw new BaseException(ErrorCode.NO_JWT_TOKEN);
		}

		if(!authorizationHeader.startsWith("Bearer ")) {
			throw new BaseException(ErrorCode.UNKNOWN_JWT_EXCEPTION);
		}

		// 전송받은 값에서 'Bearer ' 뒷부분(Jwt Token) 추출
		String token = authorizationHeader.split(" ")[1];

		boolean isLogin;

		try {
			isLogin = JwtProvider.validateToken(token);
		} catch (JwtException ex) {
			String message = ex.getMessage();
			if(ErrorCode.MALFORMED_JWT_EXCEPTION.getMessage().equals(message)) {
				throw new BaseException(ErrorCode.MALFORMED_JWT_EXCEPTION);
			}
			//잘못된 타입의 토큰인 경우
			else if(ErrorCode.WRONG_TYPE_JWT_EXCEPTION.getMessage().equals(message)) {
				throw new BaseException(ErrorCode.WRONG_TYPE_JWT_EXCEPTION);
			}
			//토큰 만료된 경우
			else if(ErrorCode.EXPIRED_JWT_EXCEPTION.getMessage().equals(message)) {
				throw new BaseException(ErrorCode.EXPIRED_JWT_EXCEPTION);
			}
			//지원되지 않는 토큰인 경우
			else {
				throw new BaseException(ErrorCode.UNKNOWN_JWT_EXCEPTION);
			}
		}



		Integer loginId = JwtProvider.getUserId(token);
		UserDetailDto loginUser=getUser(Long.valueOf(loginId));

		return new AuthResponseDto(loginUser.Id(),isLogin);
	}
}
