package com.cheerup.moomul.domain.quiz.entity;

import java.util.ArrayList;
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
@RedisHash(value = "room")
public class Room {

	@Id
	private String username;
	private int numOfPeople;
	private int numOfQuiz;
	@Builder.Default
	private boolean started = false;
	@Builder.Default
	private List<Participant> participants = new ArrayList<>();

	public void join(Participant participant) {
		participants.add(participant);
	}


	public int getNumOfParticipants() {
		return participants.size();
	}

	public boolean duplicatedNickname(String nickname){
		return participants.stream().anyMatch(participant -> participant.getNickname().equals(nickname));
	}

	public boolean isHost(String username){
		return username.equals(this.username);
	}

	public boolean deleteParticipant(String nickname) {
		return participants.removeIf(participant -> participant.getNickname().equals(nickname));
	}
}
