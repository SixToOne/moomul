package com.cheerup.moomul.domain.quiz.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "quiz")
public class QuizInfo {

	@Id
	private String username;
	private int curQuizNum;
	private List<Quiz> quizList;
}
