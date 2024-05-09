package com.cheerup.moomul.domain.quiz.dto;

import java.util.List;

import com.cheerup.moomul.domain.quiz.entity.Rank;

public record ResultResponseDto(
	String type,
	Rank myrank,
	List<Rank> total
) {
}
