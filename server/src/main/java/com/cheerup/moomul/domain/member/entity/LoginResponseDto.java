package com.cheerup.moomul.domain.member.entity;

public record LoginResponseDto(
	Long userId,
	String accessToken,
	String refreshToken
) {
}
