package com.cheerup.moomul.domain.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cheerup.moomul.domain.member.entity.IdCheckRequestDto;
import com.cheerup.moomul.domain.member.entity.IdCheckResponseDto;
import com.cheerup.moomul.domain.member.entity.ProfileModifyRequestDto;
import com.cheerup.moomul.domain.member.entity.ProfileResponseDto;
import com.cheerup.moomul.domain.member.entity.SignUpDto;
import com.cheerup.moomul.domain.member.entity.UserDetailDto;
import com.cheerup.moomul.domain.member.service.UserService;
import com.cheerup.moomul.global.response.BaseException;
import com.cheerup.moomul.global.response.ErrorCode;

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


	@GetMapping("/profile")
	public ResponseEntity<ProfileResponseDto> profile(@RequestParam String username, @AuthenticationPrincipal UserDetailDto user){

		return ResponseEntity.status(HttpStatus.OK)
			.body(userService.profile(username,user));
	}

	@PostMapping("/id-check")
	public ResponseEntity<IdCheckResponseDto> idCheck(@RequestBody @Valid IdCheckRequestDto idCheckRequestDto){
		return ResponseEntity.status(HttpStatus.OK)
			.body(userService.idCheck(idCheckRequestDto.username()));
	}

	@PatchMapping("/profile")
	public ResponseEntity<ProfileResponseDto> modifyProfile(@RequestBody ProfileModifyRequestDto profileModifyRequestDto,
		@RequestParam String username,//피드 주인 아이디
		@AuthenticationPrincipal UserDetailDto user){
		if(user==null){
			throw new BaseException(ErrorCode.NO_JWT_TOKEN);
		}

		if(!user.username().equals(username)){
			throw new BaseException(ErrorCode.NO_AUTHORITY);
		}

		return ResponseEntity.status(HttpStatus.OK)
			.body(userService.modifyProfile(user,profileModifyRequestDto));
	}

	@PatchMapping("/profile/images")
	public ResponseEntity<ProfileResponseDto> modifyProfileImage(@RequestPart MultipartFile image,
		@RequestParam String username,//피드 주인 아이디
		@AuthenticationPrincipal UserDetailDto user){
		if(user==null){
			throw new BaseException(ErrorCode.NO_JWT_TOKEN);
		}

		if(!user.username().equals(username)){
			throw new BaseException(ErrorCode.NO_AUTHORITY);
		}

		return ResponseEntity.status(HttpStatus.OK)
			.body(userService.modifyProfileImage(user,image));
	}


}
