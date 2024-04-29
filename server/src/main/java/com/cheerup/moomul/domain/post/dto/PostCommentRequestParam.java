package com.cheerup.moomul.domain.post.dto;

public record PostCommentRequestParam(
	Long primaryOffset,
	Long subOffset,
	int limit
) {
}
