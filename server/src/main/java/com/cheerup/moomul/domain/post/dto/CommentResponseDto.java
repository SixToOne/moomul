package com.cheerup.moomul.domain.post.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record CommentResponseDto(
	Long id,
	String nickname,
	String content,
	LocalDateTime createdAt,
	List<CommentResponseDto> replies
) {
	public CommentResponseDto(Long id, String nickname, String content, LocalDateTime createdAt) {
		this(id, nickname, content, createdAt, new ArrayList<>());
	}

}
