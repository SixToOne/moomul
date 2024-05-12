package com.cheerup.moomul.domain.quiz.dto;

import java.util.List;

public record ResultResponseDto(
	String type,
	RankDto myrank,
	List<RankDto> total
) {
}
