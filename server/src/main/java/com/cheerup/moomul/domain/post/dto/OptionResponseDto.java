package com.cheerup.moomul.domain.post.dto;

import com.cheerup.moomul.domain.post.entity.Option;

public record OptionResponseDto(
	Long id,
	String content,
	Long voteCnt
) {
	public static OptionResponseDto from(Option o) {
		return new OptionResponseDto(
			o.getId(),
			o.getContent(),
			o.getVoteCnt());
	}
}
