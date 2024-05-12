package com.cheerup.moomul.domain.quiz.dto;

public record QuizDto(
	String question,
	String option1,
	String option2,
	int option1Count,
	int option2Count
) {
}
