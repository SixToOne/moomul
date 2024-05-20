package com.cheerup.moomul.domain.member.entity;

public record ProfileResponseDto(
	String nickname,
	String content,
	String image,
	Boolean isMine,
	Long toMe,
	Long fromMe,
	int today
) {
}
