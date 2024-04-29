package com.cheerup.moomul.domain.member.entity;

public record LoginResponseDto(
	Long id,
	String accessToken,
	String refreshToken
) {
}
