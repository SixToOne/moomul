package com.cheerup.moomul.domain.member.entity;

import jakarta.validation.constraints.NotNull;

public record SignUpDto(
	@NotNull
	String username,
	@NotNull
	String password,
	@NotNull
	String nickname
) {
}
