package com.cheerup.moomul.domain.quiz.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

import com.cheerup.moomul.domain.quiz.dto.CancelRequestDto;
import com.cheerup.moomul.domain.quiz.dto.CreateRequestDto;
import com.cheerup.moomul.domain.quiz.dto.JoinRequestDto;
import com.cheerup.moomul.domain.quiz.dto.ResultRequestDto;
import com.cheerup.moomul.domain.quiz.dto.SubmitRequestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RestController
@RequiredArgsConstructor
public class QuizController {

	private final SimpMessageSendingOperations sendingOperations;
	private final RedisTemplate<Object, Object> redisTemplate;

	@MessageMapping("/quiz/{userId}/create")
	public void create(@DestinationVariable Long userId, CreateRequestDto createRequestDto, @Header("Authorization") String authorization) {
	}

	@MessageMapping("/quiz/{userId}/join")
	public void join(@DestinationVariable Long userId, JoinRequestDto joinRequestDto) {

	}

	@MessageMapping("/quiz/{userId}/start")
	public void start(@DestinationVariable Long userId, JoinRequestDto joinRequestDto, @Header("Authorization") String authorization) {

	}

	@MessageMapping("/quiz/{userId}/hostCancel")
	public void hostCancel(@DestinationVariable Long userId, CancelRequestDto cancelRequestDto, @Header("Authorization") String authorization) {

	}

	@MessageMapping("/quiz/{userId}/participantCancel")
	public void participantCancel(@DestinationVariable Long userId, CancelRequestDto cancelRequestDto, @Header("Authorization") String authorization) {

	}
	@MessageMapping("/quiz/{userId}/submit")
	public void submit(@DestinationVariable Long userId, SubmitRequestDto submitRequestDto) {

	}

	@MessageMapping("/quiz/{userId}/grade")
	public void grade(@DestinationVariable Long userId) {

	}

	@MessageMapping("/quiz/{userId}/next")
	public void next(@DestinationVariable Long userId) {

	}

	@MessageMapping("/quiz/{userId}/result")
	public void result(@DestinationVariable Long userId, ResultRequestDto resultRequestDto) {

	}





}
