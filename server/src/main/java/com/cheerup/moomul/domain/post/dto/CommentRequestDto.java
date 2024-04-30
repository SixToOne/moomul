package com.cheerup.moomul.domain.post.dto;

public record CommentRequestDto(
	Long parentId,
	String content
) {
}
