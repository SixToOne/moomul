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
@RedisHash(value = "party")
public class Party {

	@Id
	private Long userId;
	private List<Participant> participants;

	public void join(Participant participant) {
		participants.add(participant);
	}

	public void cancel(String nickname) {
		participants.removeIf(participant -> participant.getNickname().equals(nickname));
	}
}
