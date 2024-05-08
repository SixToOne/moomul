package com.cheerup.moomul.domain.quiz.dto;

import java.time.LocalDateTime;

public record WaitingResponse(
	String type,
	String hostname,
	int joinPeople,
	int numOfPeople,
	LocalDateTime expiredTime
) {
}
