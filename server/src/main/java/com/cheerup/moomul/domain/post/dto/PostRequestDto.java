package com.cheerup.moomul.domain.post.dto;

import java.util.List;

public record PostRequestDto(
	String nickname,
	String content,
	List<String> options
) {
}
