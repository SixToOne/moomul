package com.cheerup.moomul.domain.member.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.cheerup.moomul.domain.member.entity.SignUpDto;
import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.repository.UserRepository;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserControllerTest {

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
	void signUp() {
		//Given
		SignUpDto signUpDto = new SignUpDto("test", "test", "test");
		HttpHeaders signHeaders = new HttpHeaders();
		signHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<SignUpDto> LoginRequestEntity = new HttpEntity<>(signUpDto, signHeaders);

		//when
		ResponseEntity<Void> signUp = restTemplate.postForEntity("/users/signup", LoginRequestEntity, Void.class);

		// Then
		assertEquals(HttpStatus.CREATED, signUp.getStatusCode());
	}

	@Test
	void profile() {
	}

	@Test
	void idCheck() {
	}

	@Test
	void modifyProfile() {
	}

	@Test
	void modifyProfileImage() {
	}
}