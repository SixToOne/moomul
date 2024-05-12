package com.cheerup.moomul.domain.quiz.dto;

import java.time.LocalDateTime;

public record SubmitResponseDto(
	String type,
	int submitPeople,
	int numbOfPeople
) {
}
