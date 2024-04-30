package com.cheerup.moomul.domain.post.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cheerup.moomul.domain.member.entity.UserDetailDto;
import com.cheerup.moomul.domain.post.dto.PostRequestDto;
import com.cheerup.moomul.domain.post.dto.PostResponseDto;
import com.cheerup.moomul.domain.post.service.FromMeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(("/users/{userId}/fromme"))
public class FromMeController {

	private final FromMeService fromMeService;

	@PostMapping
	public ResponseEntity<Void> postFromMe(@PathVariable Long userId,
		@RequestBody PostRequestDto postRequestDto) {
		fromMeService.createFromMe(userId, postRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<PostResponseDto>> getFromMeFeed(@AuthenticationPrincipal UserDetailDto user,
		@PathVariable Long userId, Pageable pageable) {
		return ResponseEntity.ok(fromMeService.getFromMeFeed(user, userId, pageable));
	}

	@GetMapping("/{frommeId}")
	public ResponseEntity<PostResponseDto> getFromMe(@AuthenticationPrincipal UserDetailDto user, @PathVariable Long frommeId,
		@PathVariable Long userId) {
		return ResponseEntity.ok(fromMeService.getFromMe(user, userId, frommeId));
	}

}
