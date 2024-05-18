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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.transaction.AfterTransaction;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.cheerup.moomul.domain.member.entity.LoginRequestDto;
import com.cheerup.moomul.domain.member.entity.LoginResponseDto;
import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.repository.UserRepository;
import com.cheerup.moomul.domain.post.dto.CommentRequestDto;
import com.cheerup.moomul.domain.post.dto.CommentResponseDto;
import com.cheerup.moomul.domain.post.entity.Comment;
import com.cheerup.moomul.domain.post.entity.Post;
import com.cheerup.moomul.domain.post.entity.PostType;
import com.cheerup.moomul.domain.post.repository.CommentRepository;
import com.cheerup.moomul.domain.post.repository.PostRepository;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ToMeControllerTest {

	@Container
	public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.3");

	@Autowired
	PostRepository postRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	TestRestTemplate restTemplate;

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

		List<Post> posts = List.of(Post.builder()
			.id(1L)
			.nickname("늘보")
			.user(user)
			.content("[ToMe]너 잠꾸러기니")
			.postType(PostType.TO_ME)
			.build());

		postRepository.saveAll(posts);

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
		//Given
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

		String userToken = login.getBody().accessToken();
		CommentRequestDto comment = new CommentRequestDto(
			null, "댓글작성");
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(userToken);
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
}