package com.cheerup.moomul.domain.quiz.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cheerup.moomul.domain.quiz.entity.Quiz;
import com.cheerup.moomul.domain.quiz.repository.QuizRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuizService {

	private final QuizRepository quizRepository;

	public List<Quiz> findRandomQuiz(int limit) {
		return quizRepository.findRandomQuiz(limit);
	}
}
