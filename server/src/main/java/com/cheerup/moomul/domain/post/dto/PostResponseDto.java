package com.cheerup.moomul.domain.post.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponseDto(
	Long id,
	String nickname,
	String content,
	List<OptionResponseDto> options,
	Long voted,
	String reply,
	Long likeCnt,
	Long commentCnt,
	boolean liked,
	LocalDateTime createdAt
) {

}
