package com.cheerup.moomul.domain.quiz.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rank {
	Integer rank;
	String nickname;
	Integer score;
}

