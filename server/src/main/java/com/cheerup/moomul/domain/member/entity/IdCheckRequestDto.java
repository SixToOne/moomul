package com.cheerup.moomul.domain.member.entity;

import jakarta.validation.constraints.NotBlank;

public record IdCheckRequestDto(
	@NotBlank
	String username
) {
}
