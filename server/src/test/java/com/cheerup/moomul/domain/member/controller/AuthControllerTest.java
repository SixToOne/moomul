package com.cheerup.moomul.domain.member.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.cheerup.moomul.domain.member.entity.LoginRequestDto;
import com.cheerup.moomul.domain.member.entity.LoginResponseDto;
import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.repository.UserRepository;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthControllerTest {

	@Autowired
	TestRestTemplate restTemplate;

	@Autowired
	UserRepository userRepository;

	@BeforeEach
	void setUp() {
		User user = User.builder()
			.id(1L)
			.username("늘보")
			.password("늘보")
			.content("잠꾸러기 늘보 채널입니다.")
			.nickname("늘보")
			.build();

		userRepository.save(user);
	}

	@Test
	void login() {
		//Given
		LoginRequestDto loginRequestDto = new LoginRequestDto("늘보", "늘보");
		HttpHeaders loginHeaders = new HttpHeaders();
		loginHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LoginRequestDto> LoginRequestEntity = new HttpEntity<>(loginRequestDto, loginHeaders);

		//when
		ResponseEntity<LoginResponseDto> login = restTemplate.exchange(
			"/users/login",
			HttpMethod.POST,
			LoginRequestEntity,
			new ParameterizedTypeReference<LoginResponseDto>() {
			}
		);

		// Then
		assertEquals(HttpStatus.OK, login.getStatusCode());
	}
}