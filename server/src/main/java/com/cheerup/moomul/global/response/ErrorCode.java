package com.cheerup.moomul.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	INTERNAL_SERVER_ERROR(500, "COMMON-001", "서버에서 처리할 수 없는 경우");

	private final int status;
	private final String code;
	private final String message;

}
