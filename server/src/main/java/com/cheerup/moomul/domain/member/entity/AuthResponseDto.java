package com.cheerup.moomul.domain.member.entity;

public record AuthResponseDto(
	Long userId,
	Boolean isLogin
) {
}
