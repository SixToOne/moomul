package com.cheerup.moomul.domain.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cheerup.moomul.domain.member.entity.AuthResponseDto;
import com.cheerup.moomul.domain.member.entity.LoginRequestDto;
import com.cheerup.moomul.domain.member.entity.LoginResponseDto;
import com.cheerup.moomul.domain.member.service.UserDetailService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class AuthController {
	private final UserDetailService userDetailService;

	@PostMapping("/auth")
	public ResponseEntity<AuthResponseDto> auth(HttpServletRequest request){

		return ResponseEntity.status(HttpStatus.OK)
			.body(userDetailService.auth(request));
	}
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
		return ResponseEntity.status(HttpStatus.OK)
			.body(userDetailService.login(loginRequestDto));
	}
}
