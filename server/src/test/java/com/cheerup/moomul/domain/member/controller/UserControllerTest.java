package com.cheerup.moomul.domain.member.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Objects;

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
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.cheerup.moomul.domain.member.entity.IdCheckRequestDto;
import com.cheerup.moomul.domain.member.entity.IdCheckResponseDto;
import com.cheerup.moomul.domain.member.entity.LoginRequestDto;
import com.cheerup.moomul.domain.member.entity.LoginResponseDto;
import com.cheerup.moomul.domain.member.entity.ProfileResponseDto;
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

	@Container
	public static GenericContainer<?> redis =
		new GenericContainer<>(DockerImageName.parse("redis:6.2.6"));

	static String accessToken;

	@BeforeEach
	void setUp() {
		redis.start();

		List<User> users = List.of(User.builder()
				.id(1L)
				.username("늘보")
				.password("늘보")
				.content("잠꾸러기 늘보 채널입니다.")
				.nickname("늘보")
				.build(),

			User.builder()
				.id(2L)
				.username("computer")
				.password("computer")
				.content("computer")
				.nickname("computer")
				.build()

		);

		userRepository.saveAll(users);

		//로그인
		LoginRequestDto loginRequestDto = new LoginRequestDto("computer", "computer");
		HttpHeaders loginHeaders = new HttpHeaders();
		loginHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LoginRequestDto> LoginRequestEntity = new HttpEntity<>(loginRequestDto, loginHeaders);

		ResponseEntity<LoginResponseDto> login = restTemplate.exchange(
			"/users/login",
			HttpMethod.POST,
			LoginRequestEntity,
			new ParameterizedTypeReference<LoginResponseDto>() {
			}
		);

		accessToken = login.getBody().accessToken();

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
		//Given
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(accessToken);
		HttpEntity<String> requestEntity = new HttpEntity<>(headers);

		//when
		ResponseEntity<ProfileResponseDto> response = restTemplate.exchange(
			"/users/profile?username=늘보",
			HttpMethod.GET,
			requestEntity,
			new ParameterizedTypeReference<ProfileResponseDto>() {
			}
		);
		// Then
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertThat(Objects.requireNonNull(response.getBody()).today()).isEqualTo(1);

	}

	@Test
	void idCheck() {
		//Given
		IdCheckRequestDto idCheckRequestDto = new IdCheckRequestDto("hasNotName");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<IdCheckRequestDto> idCheckEntity = new HttpEntity<>(idCheckRequestDto, headers);

		//when
		ResponseEntity<IdCheckResponseDto> response = restTemplate.exchange(
			"/users/id-check",
			HttpMethod.POST,
			idCheckEntity,
			new ParameterizedTypeReference<IdCheckResponseDto>() {
			}
		);

		// Then
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertThat(Objects.requireNonNull(response.getBody()).isValid()).isEqualTo(true);
	}

	@Test
	void modifyProfile() {
	}

	@Test
	void modifyProfileImage() {
	}
}