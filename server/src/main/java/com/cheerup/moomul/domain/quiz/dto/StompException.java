package com.cheerup.moomul.domain.quiz.dto;

import com.cheerup.moomul.global.response.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StompException extends RuntimeException {
	private final String message;
}
