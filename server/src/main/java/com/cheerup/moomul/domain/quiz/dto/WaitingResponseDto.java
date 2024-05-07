package com.cheerup.moomul.domain.quiz.dto;

import java.time.LocalDateTime;

public record WaitingResponseDto(
	String type,
	String hostname,
	int joinPeople,
	int numbOfPeople,
	LocalDateTime expiredTime
) {
}
