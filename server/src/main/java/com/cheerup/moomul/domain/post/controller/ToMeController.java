package com.cheerup.moomul.domain.post.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cheerup.moomul.domain.post.dto.CommentResponseDto;
import com.cheerup.moomul.domain.post.dto.PostCommentRequestParam;
import com.cheerup.moomul.domain.post.service.ToMeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping(("/users/{userId}/tome"))
@Slf4j
public class ToMeController {

	private final ToMeService toMeService;

	@GetMapping("/{tomeId}/comments")
	ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable Long userId, @PathVariable Long tomeId) {
		PostCommentRequestParam param = new PostCommentRequestParam(1L, 1L, 1);
		List<CommentResponseDto> result = toMeService.getComments(tomeId, param);
		return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
	}

}
