package com.cheerup.moomul.domain.member.entity;

public record ProfileDto(
	String nickname,
	String content,
	String image,
	Long toMe,
	Long fromMe

) {
}
