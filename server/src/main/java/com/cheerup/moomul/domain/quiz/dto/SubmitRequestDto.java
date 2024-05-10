package com.cheerup.moomul.domain.quiz.dto;

public record SubmitRequestDto(
	String nickname,
	String answer
) {
	public boolean equalNickname(String username){
		return nickname.equals(username);
	}
	public boolean equalAnswer(String inputAnswer){
		return answer.equals(inputAnswer);
	}
}
