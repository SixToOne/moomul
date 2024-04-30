package com.cheerup.moomul.domain.member.jwt;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cheerup.moomul.domain.member.entity.UserDetailDto;
import com.cheerup.moomul.domain.member.service.UserDetailService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final UserDetailService userDetailService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		if(authorizationHeader == null) {
			filterChain.doFilter(request, response);
			return;
		}

		// Header의 Authorization 값이 'Bearer '로 시작하지 않으면 => 잘못된 토큰
		if(!authorizationHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		// 전송받은 값에서 'Bearer ' 뒷부분(Jwt Token) 추출
		String token = authorizationHeader.split(" ")[1];

		if(JwtProvider.validateToken(token)) {

			// Jwt Token에서 loginId 추출
			Integer loginId = JwtProvider.getUserId(token);

			// 추출한 loginId로 User 찾아오기
			UserDetailDto loginUser = userDetailService.getUser(Long.valueOf(loginId));

			// loginUser 정보로 UsernamePasswordAuthenticationToken 발급
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				loginUser, null);
			authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		}
		filterChain.doFilter(request, response);
	}
}
