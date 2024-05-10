package com.cheerup.moomul.domain.quiz.dto;

public record CreateRequestDto(
	String nickname,
	int numOfPeople,
	int numOfQuiz
) {
}
