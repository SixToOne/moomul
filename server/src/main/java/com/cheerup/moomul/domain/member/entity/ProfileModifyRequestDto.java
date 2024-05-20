package com.cheerup.moomul.domain.member.entity;

public record ProfileModifyRequestDto(
	String nickname,
	String content
) {
}
