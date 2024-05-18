package com.cheerup.moomul.domain.member.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.cheerup.moomul.domain.member.entity.IdCheckRequestDto;
import com.cheerup.moomul.domain.member.entity.IdCheckResponseDto;
import com.cheerup.moomul.domain.member.entity.LoginRequestDto;
import com.cheerup.moomul.domain.member.entity.LoginResponseDto;
import com.cheerup.moomul.domain.member.entity.ProfileModifyRequestDto;
import com.cheerup.moomul.domain.member.entity.ProfileResponseDto;
import com.cheerup.moomul.domain.member.entity.SignUpDto;
import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.repository.UserRepository;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserControllerTest {

	@Autowired
	private WebClient.Builder webClientBuilder;

	WebClient webClient;

	@Autowired
	TestRestTemplate restTemplate;

	@Autowired
	UserRepository userRepository;

	@Container
	public static GenericContainer<?> redis =
		new GenericContainer<>(DockerImageName.parse("redis:6.2.6"));

	@Rule
	public LocalStackContainer localStack = new LocalStackContainer(
		DockerImageName.parse("localstack/localstack:0.11.3"))
		.withServices(S3);

	@LocalServerPort
	private int port;

	static String accessToken;

	@BeforeEach
	void setUp() {
		this.webClient = webClientBuilder.baseUrl("http://localhost:" + port).build();

		redis.start();
		localStack.start();

		AmazonS3 s3 = AmazonS3ClientBuilder
			.standard()
			.withEndpointConfiguration(
				new AwsClientBuilder.EndpointConfiguration(
					localStack.getEndpoint().toString(),
					localStack.getRegion()
				)
			)
			.withCredentials(
				new AWSStaticCredentialsProvider(
					new BasicAWSCredentials(localStack.getAccessKey(), localStack.getSecretKey())
				)
			)
			.build();
		// s3.createBucket(CreateBucketRequest.builder().bucket("togeduck").build());
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
		//Given
		ProfileModifyRequestDto profileModifyRequestDto = new ProfileModifyRequestDto("ImComputer", "testing");

		//When
		ProfileResponseDto response = webClient.patch()
			.uri(uriBuilder -> uriBuilder.path("/api/users/profile")
				.queryParam("username", "computer")
				.build())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(profileModifyRequestDto))
			.retrieve()
			.bodyToMono(ProfileResponseDto.class)
			.block();

		// Then
		assertNotNull(response);
		assertEquals("ImComputer", response.nickname());
		assertEquals("testing", response.content());

	}

	@Test
	void modifyProfileImage() {

		// Given
		MockMultipartFile imageFile = new MockMultipartFile(
			"testImage", "image.jpg", MediaType.IMAGE_JPEG_VALUE,
			"Test Image Content".getBytes(StandardCharsets.UTF_8));

		// When
		ProfileResponseDto response = webClient.patch()
			.uri(uriBuilder -> uriBuilder.path("/api/users/profile/images")
				.queryParam("username", "computer")
				.build())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.body(BodyInserters.fromMultipartData("image", imageFile.getResource()))
			.retrieve()
			.bodyToMono(ProfileResponseDto.class)
			.block();

		// Then
		assertNotNull(response);
		assertNotNull(response.image());
	}
}