package com.cheerup.moomul.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	INTERNAL_SERVER_ERROR(500, "COMMON-001", "서버에서 처리할 수 없는 경우"),
	NO_USER_ERROR(403, "USER-001", "유저가 없음"),
	WRONG_PASSWORD(400, "USER-002", "비밀번호 틀림"),
	DUPLICATE_USER_ERROR(400, "USER-004", "이미 사용중인 아이디"),
	MALFORMED_JWT_EXCEPTION(403, "JWT-001", "지원되지않는 토큰"),
	EXPIRED_JWT_EXCEPTION(403, "JWT-002", "만료된 토큰"),
	WRONG_TYPE_JWT_EXCEPTION(403, "JWT-003", "잘못된 타입의 토큰"),
	UNKNOWN_JWT_EXCEPTION(403, "JWT-004", "이상한 값의 토큰"),
	NO_JWT_TOKEN(403, "JWT-005", "토큰이 없음"),
	NO_AUTHORITY(401, "JWT-006", "접근 권한이 없음"),
	NO_LOGIN(401, "JWT-006", "로그인이 되어있지 않음"),
	NO_POST_ERROR(404, "POST-001", "게시글이 없음"),
	NO_COMMENT_ERROR(404, "COMMENT-001", "존재 하지 않은 댓글 아이디"),
	NO_OPTION_ERROR(404, "OPTION-001", "투표 옵션이 없을 경우");

	private final int status;
	private final String code;
	private final String message;

}
