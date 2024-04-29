package com.cheerup.moomul.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	INTERNAL_SERVER_ERROR(500, "COMMON-001", "서버에서 처리할 수 없는 경우"),
	NO_USER_ERROR(403, "USER-001", "유저가 없음"),
	WRONG_PASSWORD(400,"USER-002","비밀번호 틀림"),
	MALFORMED_JWT_EXCEPTION(403,"JWT-001" ,"지원되지않는 토큰"),
	EXPIRED_JWT_EXCEPTION(403,"JWT-002" ,"만료된 토큰"),
	WRONG_TYPE_JWT_EXCEPTION(403,"JWT-003" ,"잘못된 타입의 토큰"),
	UNKNOWN_JWT_EXCEPTION(403,"JWT-004" ,"이상한 값의 토큰");


	private final int status;
	private final String code;
	private final String message;

}
