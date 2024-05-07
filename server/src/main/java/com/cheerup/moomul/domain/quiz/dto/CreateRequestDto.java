package com.cheerup.moomul.domain.quiz.dto;

public record CreateRequestDto(
	Long numberOfPeople,
	Long numOfQuiz
) {
}
