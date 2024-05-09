package com.cheerup.moomul.global.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.cheerup.moomul.domain.quiz.dto.StompErrorResponse;
import com.cheerup.moomul.domain.quiz.dto.StompException;
import com.cheerup.moomul.global.response.BaseException;
import com.cheerup.moomul.global.response.ErrorResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
	}

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
		return ResponseEntity.status(e.getStatus()).body(new ErrorResponse(e.getStatus(), e.getCode()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidatedException(MethodArgumentNotValidException e){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "입력값이 유효하지 않음(null 확인)"));
	}

	@MessageExceptionHandler
	@SendToUser("/errors")
	public StompErrorResponse handleException(StompException exception) {
		System.out.println("GlobalExceptionHandler.handleException");
		return new StompErrorResponse("error", exception.getMessage());
	}

	@MessageExceptionHandler
	@SendToUser("/errors")
	public StompErrorResponse handleException(BaseException e) {
		System.out.println("GlobalExceptionHandler.handleException");
		return new StompErrorResponse("error", e.getMessage());
	}


}
