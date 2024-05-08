package com.cheerup.moomul.domain.quiz.entity;

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
@RedisHash(value = "room")
public class Room {

	@Id
	private Long userId;
	private int numOfPeople;
	private int numOfQuiz;
	private boolean started;
	private String nickname;
}
