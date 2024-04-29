package com.cheerup.moomul.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	INTERNAL_SERVER_ERROR(500, "COMMON-001", "서버에서 처리할 수 없는 경우"),
	POST_NOT_FOUND(404, "POST-001", "게시글이 없는 경우"),
	USER_NOT_FOUND(404, "USER-001", "유저가 없는 경우");

	private final int status;
	private final String code;
	private final String message;

}
