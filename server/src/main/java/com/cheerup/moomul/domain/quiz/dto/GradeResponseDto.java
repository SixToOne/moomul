package com.cheerup.moomul.domain.quiz.dto;

import java.time.LocalDateTime;
import java.util.List;

public record GradeResponseDto(
	String type,
	QuizDto quiz,
	int quizNum,
	int numOfQuiz,
	LocalDateTime expiredTime,
	String hostAnswer,
	String myAnswer,
	List<RankDto> result
) {
}
