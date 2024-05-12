package com.cheerup.moomul.domain.quiz.dto;

import java.time.LocalDateTime;

public record CancelResponseDto(
	String type,
	String message
) {
}
