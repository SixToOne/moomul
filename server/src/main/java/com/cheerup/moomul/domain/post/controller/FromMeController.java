package com.cheerup.moomul.domain.post.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cheerup.moomul.domain.member.entity.UserDetailDto;
import com.cheerup.moomul.domain.post.dto.CommentRequestDto;
import com.cheerup.moomul.domain.post.dto.CommentResponseDto;
import com.cheerup.moomul.domain.post.dto.PostCommentRequestParam;
import com.cheerup.moomul.domain.post.dto.PostLikeResponseDto;
import com.cheerup.moomul.domain.post.dto.PostRequestDto;
import com.cheerup.moomul.domain.post.dto.PostResponseDto;
import com.cheerup.moomul.domain.post.dto.VoteRequestDto;
import com.cheerup.moomul.domain.post.service.FromMeService;
import com.cheerup.moomul.global.response.BaseException;
import com.cheerup.moomul.global.response.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(("/fromme"))
public class FromMeController {
	private final FromMeService fromMeService;

	@DeleteMapping("/{frommeId}")
	public ResponseEntity<Void> deleteFromMe(@AuthenticationPrincipal UserDetailDto user, @RequestParam String username,
		@PathVariable Long frommeId) {
		if (user == null) {
			throw new BaseException(ErrorCode.NO_JWT_TOKEN);
		}

		if (!user.username().equals(username)) {
			throw new BaseException(ErrorCode.NO_AUTHORITY);
		}

		fromMeService.removeFromMe(username, frommeId);
		return ResponseEntity.ok().build();
	}

	@PostMapping
	public ResponseEntity<Void> postFromMe(@RequestParam String username,
		@RequestBody PostRequestDto postRequestDto) {
		fromMeService.createFromMe(username, postRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<PostResponseDto>> getFromMeFeed(@AuthenticationPrincipal UserDetailDto user,
		@RequestParam String username, Pageable pageable) {
		return ResponseEntity.ok(fromMeService.getFromMeFeed(user, username, pageable));
	}

	@GetMapping("/{frommeId}")
	public ResponseEntity<PostResponseDto> getFromMe(@AuthenticationPrincipal UserDetailDto user,
		@PathVariable Long frommeId,
		@RequestParam String username) {
		return ResponseEntity.ok(fromMeService.getFromMe(user, username, frommeId));
	}

	@PostMapping("/{frommeId}/likes")
	public ResponseEntity<PostLikeResponseDto> postFromMeLikes(@AuthenticationPrincipal UserDetailDto user,
		@RequestParam String username,
		@PathVariable Long frommeId) {
		if (user == null) {
			throw new BaseException(ErrorCode.NO_JWT_TOKEN);
		}

		PostLikeResponseDto likeCnt = fromMeService.likeFromMe(user, username, frommeId);
		return ResponseEntity.ok().body(likeCnt);
	}

	@PatchMapping("/{frommeId}/votes")
	public ResponseEntity<PostResponseDto> voteFromMe(@AuthenticationPrincipal UserDetailDto user,
		@RequestBody VoteRequestDto voted,
		@RequestParam String username,
		@PathVariable Long frommeId) {
		if (user == null) {
			throw new BaseException(ErrorCode.NO_JWT_TOKEN);
		}

		PostResponseDto dto = fromMeService.selectFromMeVote(voted, username, frommeId, user);
		return new ResponseEntity<>(dto, HttpStatus.ACCEPTED);
	}

	@GetMapping("/{frommeId}/comments")
	ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable Long frommeId) {
		PostCommentRequestParam param = new PostCommentRequestParam(1L, 1L, 1);
		List<CommentResponseDto> result = fromMeService.getComments(frommeId, param);
		return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
	}

	@PostMapping("{frommeId}/comments")
	ResponseEntity<List<CommentResponseDto>> postComments(@AuthenticationPrincipal UserDetailDto user,
		@PathVariable Long frommeId, @RequestBody CommentRequestDto comment) {

		if (user == null) {
			throw new BaseException(ErrorCode.NO_LOGIN);
		}

		fromMeService.createComments(user, frommeId, comment); //현재 로그인 user, 게시글 userId, 게시글 Id

		PostCommentRequestParam param = new PostCommentRequestParam(1L, 1L, 1);
		List<CommentResponseDto> result = fromMeService.getComments(frommeId, param);
		return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
	}
}
