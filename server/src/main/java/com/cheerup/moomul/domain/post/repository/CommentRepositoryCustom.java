package com.cheerup.moomul.domain.post.repository;

import java.util.List;

import com.cheerup.moomul.domain.post.dto.CommentResponseDto;
import com.cheerup.moomul.domain.post.dto.PostCommentRequestParam;

public interface CommentRepositoryCustom {
	List<CommentResponseDto> findCommentByPostId(Long postId, PostCommentRequestParam param);
}
