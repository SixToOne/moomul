package com.cheerup.moomul.domain.quiz.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.cheerup.moomul.domain.quiz.entity.Quiz;
import com.cheerup.moomul.domain.quiz.entity.QuizInfo;

public interface QuizInfoRepository extends CrudRepository<QuizInfo, String> {

}
