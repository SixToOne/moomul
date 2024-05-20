package com.cheerup.moomul.domain.member.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignUpDto(
	@NotBlank
	String username,
	@NotBlank
	String password,
	@NotBlank
	String nickname
) {
}
