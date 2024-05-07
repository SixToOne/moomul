package com.cheerup.moomul.domain.quiz.dto;

import java.time.LocalDateTime;

import com.cheerup.moomul.domain.quiz.entity.Quiz;

public record QuizResponseDto(
	String type,
	QuizDto quiz,
	int quizNum,
	int numOfQuiz,

	LocalDateTime expiredTime
) {
}
