package com.cheerup.moomul.domain.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cheerup.moomul.domain.member.entity.IdCheckRequestDto;
import com.cheerup.moomul.domain.member.entity.IdCheckResponseDto;
import com.cheerup.moomul.domain.member.entity.LoginRequestDto;
import com.cheerup.moomul.domain.member.entity.LoginResponseDto;
import com.cheerup.moomul.domain.member.entity.ProfileResponseDto;
import com.cheerup.moomul.domain.member.entity.SignUpDto;
import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
	private final UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpDto signUpDto){
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(userService.signUp(signUpDto));
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
		return ResponseEntity.status(HttpStatus.OK)
			.body(userService.login(loginRequestDto));
	}

	@GetMapping("/{userId}")
	public ResponseEntity<ProfileResponseDto> profile(@PathVariable Long userId, @AuthenticationPrincipal User user){
		return ResponseEntity.status(HttpStatus.OK)
			.body(userService.profile(userId,user));
	}

	@PostMapping("/id-check")
	public ResponseEntity<IdCheckResponseDto> idCheck(@RequestBody IdCheckRequestDto idCheckRequestDto){
		return ResponseEntity.status(HttpStatus.OK)
			.body(userService.idCheck(idCheckRequestDto.username()));
	}

}
