package com.cheerup.moomul.domain.member.jwt;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cheerup.moomul.global.response.ErrorCode;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws
		ServletException, IOException {
		try {
			chain.doFilter(request, response);
		} catch (JwtException ex) {
			String message = ex.getMessage();
			if(ErrorCode.MALFORMED_JWT_EXCEPTION.getMessage().equals(message)) {
				setResponse(response, ErrorCode.MALFORMED_JWT_EXCEPTION);
			}
			//잘못된 타입의 토큰인 경우
			else if(ErrorCode.WRONG_TYPE_JWT_EXCEPTION.getMessage().equals(message)) {
				setResponse(response, ErrorCode.WRONG_TYPE_JWT_EXCEPTION);
			}
			//토큰 만료된 경우
			else if(ErrorCode.EXPIRED_JWT_EXCEPTION.getMessage().equals(message)) {
				setResponse(response, ErrorCode.EXPIRED_JWT_EXCEPTION);
			}
			//지원되지 않는 토큰인 경우
			else if(ErrorCode.UNKNOWN_JWT_EXCEPTION.getMessage().equals(message)){
				setResponse(response, ErrorCode.UNKNOWN_JWT_EXCEPTION);
			}
			//토큰이 없는 경우
			else if(ErrorCode.NO_JWT_TOKEN.getMessage().equals(message)){
				setResponse(response,ErrorCode.NO_JWT_TOKEN);
			}
		}
	}

	private void setResponse(HttpServletResponse response, ErrorCode errorMessage) throws RuntimeException, IOException {
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(errorMessage.getStatus());
		response.getWriter().print(errorMessage.getMessage());
	}
}