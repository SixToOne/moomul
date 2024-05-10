package com.cheerup.moomul.domain.quiz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cheerup.moomul.domain.quiz.entity.Quiz;

public interface QuizRepository extends JpaRepository<Quiz, Long>{
	@Query(value = "SELECT * FROM quiz order by RAND() limit :limit",nativeQuery = true)
	List<Quiz> findRandomQuiz(@Param("limit") int limit);
}
