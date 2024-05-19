package com.cheerup.moomul.domain.post.controller;

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
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.cheerup.moomul.domain.member.entity.LoginRequestDto;
import com.cheerup.moomul.domain.member.entity.LoginResponseDto;
import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.repository.UserRepository;
import com.cheerup.moomul.domain.post.dto.CommentRequestDto;
import com.cheerup.moomul.domain.post.dto.CommentResponseDto;
import com.cheerup.moomul.domain.post.dto.PostRequestDto;
import com.cheerup.moomul.domain.post.dto.PostResponseDto;
import com.cheerup.moomul.domain.post.dto.ReplyRequestDto;
import com.cheerup.moomul.domain.post.dto.VoteRequestDto;
import com.cheerup.moomul.domain.post.entity.Comment;
import com.cheerup.moomul.domain.post.entity.Option;
import com.cheerup.moomul.domain.post.entity.Post;
import com.cheerup.moomul.domain.post.entity.PostType;
import com.cheerup.moomul.domain.post.repository.CommentRepository;
import com.cheerup.moomul.domain.post.repository.OptionRepository;
import com.cheerup.moomul.domain.post.repository.PostRepository;
import com.cheerup.moomul.domain.post.repository.VoteRepository;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ToMeControllerTest {

	@Container
	public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.3");

	@Autowired
	private WebClient.Builder webClientBuilder;

	WebClient webClient;

	@Autowired
	PostRepository postRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	OptionRepository optionRepository;

	@Autowired
	VoteRepository voteRepository;

	@Autowired
	TestRestTemplate restTemplate;

	static String accessToken;

	@LocalServerPort
	private int port;

	@BeforeEach
	void setUp() {
		this.webClient = webClientBuilder.baseUrl("http://localhost:" + port).build();

		User user = User.builder()
			.id(1L)
			.username("늘보")
			.password("늘보")
			.content("잠꾸러기 늘보 채널입니다.")
			.nickname("늘보")
			.build();

		userRepository.save(user);

		List<Post> posts = List.of(Post.builder()
				.id(1L)
				.nickname("늘보")
				.user(user)
				.content("[ToMe]너 잠꾸러기니")
				.postType(PostType.TO_ME)
				.build(),

			Post.builder()
				.id(2L)
				.nickname("늘보")
				.user(user)
				.content("고생해")
				.postType(PostType.TO_ME)
				.build()
		);

		postRepository.saveAll(posts);

		List<Option> options = List.of(Option.builder()
				.id(1L)
				.content("option1")
				.post(posts.get(0))
				.build(),

			Option.builder()
				.id(2L)
				.content("option2")
				.post(posts.get(0))
				.build());

		optionRepository.saveAll(options);

		List<Comment> comments = List.of(
			Comment.builder()
				.id(1L)
				.post(posts.get(0))
				.user(user)
				.content("잠꾸러기 아니야")
				.parent(null)
				.build(),

			Comment.builder()
				.id(3L)
				.post(posts.get(0))
				.user(user)
				.content("잠꾸러기 아니야")
				.parent(null)
				.build()
		);

		commentRepository.saveAll(comments);

		//ToMe생성
		PostRequestDto postRequestDto = new PostRequestDto("testNick", "testing", List.of("1", "2", "3"));
		ResponseEntity<Void> response = restTemplate.postForEntity("/tome?username=늘보", postRequestDto, Void.class);

		//로그인
		LoginRequestDto loginRequestDto = new LoginRequestDto("늘보", "늘보");
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
	@AfterTransaction
	void getComments() {
		//when
		ResponseEntity<List<CommentResponseDto>> response = restTemplate.exchange(
			"/tome/1/comments",
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<List<CommentResponseDto>>() {
			}
		);
		// Then
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertThat(Objects.requireNonNull(response.getBody()).size()).isEqualTo(2);
	}

	@Test
	void postComments() {

		CommentRequestDto comment = new CommentRequestDto(
			null, "댓글작성");
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		HttpEntity<CommentRequestDto> requestEntity = new HttpEntity<>(comment, headers);

		//when
		ResponseEntity<List<CommentResponseDto>> response = restTemplate.exchange(
			"/tome/1/comments",
			HttpMethod.POST,
			requestEntity,
			new ParameterizedTypeReference<List<CommentResponseDto>>() {
			}
		);

		// Then
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		assertThat(Objects.requireNonNull(response.getBody()).size()).isEqualTo(3);
	}

	@Test
	void postToMe() {
		//Given
		PostRequestDto postRequestDto = new PostRequestDto("testNick", "testing", List.of("1", "2", "3"));

		//When
		ResponseEntity<Void> response = restTemplate.postForEntity("/tome?username=늘보", postRequestDto, Void.class);

		// Then
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response);

	}

	/*TODO*/
	@Test
	void getRepliedToMe() {

		// When
		List<PostResponseDto> response = webClient.get()
			.uri(uriBuilder -> uriBuilder.path("/api/tome/replied")
				.queryParam("username", "늘보")
				.queryParam("page", 0)
				.queryParam("size", 10)
				.build())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.retrieve()
			.bodyToFlux(PostResponseDto.class)
			.collectList()
			.block();

		// Then
		assertThat(response).isNotNull();
	}

	@Test
	void getToMe() {
		// When
		PostResponseDto response = webClient.get()
			.uri(uriBuilder -> uriBuilder.path("/api/tome/1")
				.queryParam("username", "늘보")
				.build())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.retrieve()
			.bodyToMono(PostResponseDto.class)
			.block();

		//Then
		assertNotNull(response);

	}

	@Test
	void deleteToMe() {
		// When
		WebClient.ResponseSpec responseSpec = webClient.delete()
			.uri(uriBuilder -> uriBuilder.path("/api/tome/1")
				.queryParam("username", "늘보")
				.build())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.retrieve();

		// Then
		responseSpec.toBodilessEntity()
			.doOnSuccess(response -> {
				assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
			})
			.block();
	}

	@Test
	void postReplies() {
		//Given
		ReplyRequestDto replyRequestDto = new ReplyRequestDto("replyTest");

		// When
		WebClient.ResponseSpec responseSpec = webClient.post()
			.uri(uriBuilder -> uriBuilder.path("/api/tome/1/replies")
				.queryParam("username", "늘보")
				.build())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(replyRequestDto))
			.retrieve();

		// Then
		responseSpec.toBodilessEntity()
			.doOnSuccess(response -> {
				assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
			})
			.block();

	}

	@Test
	void voteToMe() {
		//Given
		VoteRequestDto voteRequestDto = new VoteRequestDto(1L);

		// When
		PostResponseDto response = webClient.patch()
			.uri(uriBuilder -> uriBuilder.path("/api/tome/1/votes")
				.queryParam("username", "늘보")
				.build())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(voteRequestDto))
			.retrieve()
			.bodyToMono(PostResponseDto.class)
			.block();

		assertNotNull(response);
		assertEquals(1L, response.voted());

	}

}