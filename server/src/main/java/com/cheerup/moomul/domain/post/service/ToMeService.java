package com.cheerup.moomul.domain.post.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.entity.UserDetailDto;
import com.cheerup.moomul.domain.member.repository.UserRepository;
import com.cheerup.moomul.domain.post.dto.CommentRequestDto;
import com.cheerup.moomul.domain.post.dto.CommentResponseDto;
import com.cheerup.moomul.domain.post.dto.PostCommentRequestParam;
import com.cheerup.moomul.domain.post.entity.Comment;
import com.cheerup.moomul.domain.post.entity.Post;
import com.cheerup.moomul.domain.post.repository.CommentRepository;
import com.cheerup.moomul.domain.post.repository.PostRepository;
import com.cheerup.moomul.global.response.BaseException;
import com.cheerup.moomul.global.response.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ToMeService {

	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;

	public List<CommentResponseDto> getComments(Long postId, PostCommentRequestParam param) {
		return commentRepository.findCommentByPostId(postId, param);
	}

	@Transactional
	public void createComments(UserDetailDto user, Long tomeId, CommentRequestDto requestDto) {
		//현재 로그인 user, 게시글 userId, 게시글 Id

		Post post = postRepository.findById(tomeId).orElseThrow(() -> new BaseException(ErrorCode.NO_POST_ERROR));
		Comment parent = null;
		if (requestDto.parentId() != null) {
			parent = commentRepository.findById(requestDto.parentId())
				.orElseThrow(() -> new BaseException(ErrorCode.NO_COMMENT_ERROR));
		}
		User loginUser = userRepository.findById(user.Id())
			.orElseThrow(() -> new BaseException(ErrorCode.NO_USER_ERROR));

		Comment comment = Comment.builder()
			.user(loginUser)
			.post(post)
			.content(requestDto.content())
			.build();

		if (parent != null) {
			comment.addParent(parent);
		}
		commentRepository.save(comment);
	}
}
