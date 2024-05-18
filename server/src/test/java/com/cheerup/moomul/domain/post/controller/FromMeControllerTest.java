package com.cheerup.moomul.domain.post.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.cheerup.moomul.domain.member.entity.LoginRequestDto;
import com.cheerup.moomul.domain.member.entity.LoginResponseDto;
import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.repository.UserRepository;
import com.cheerup.moomul.domain.post.dto.CommentRequestDto;
import com.cheerup.moomul.domain.post.dto.CommentResponseDto;
import com.cheerup.moomul.domain.post.dto.PostCommentRequestParam;
import com.cheerup.moomul.domain.post.dto.PostRequestDto;
import com.cheerup.moomul.domain.post.entity.Comment;
import com.cheerup.moomul.domain.post.entity.Post;
import com.cheerup.moomul.domain.post.entity.PostType;
import com.cheerup.moomul.domain.post.repository.CommentRepository;
import com.cheerup.moomul.domain.post.repository.OptionRepository;
import com.cheerup.moomul.domain.post.repository.PostLikeRepository;
import com.cheerup.moomul.domain.post.repository.PostRepository;
import com.cheerup.moomul.domain.post.repository.VoteRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FromMeControllerTest {
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PostRepository postRepository;

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	OptionRepository optionRepository;

	@Autowired
	VoteRepository voteRepository;

	@Autowired
	PostLikeRepository postLikeRepository;

	@BeforeEach
	void setUp() {
		// getComments 테스트 전 데이터 초기화 처리
		commentRepository.deleteAll();
	}

	@Test
	void deleteFromMe() {
		User user = userRepository.save(User.builder()
			.id(1L)
			.username("아이디")
			.nickname("닉네임")
			.password("pw")
			.content("소개문구")
			.build());

		Post post = postRepository.save(Post.builder()
			.id(1L)
			.content("내용")
			.nickname("닉네임")
			.postType(PostType.FROM_ME)
			.reply("답글")
			.user(user)
			.build());

		// 로그인 토큰 받기 (@AuthenticationPrincipal UserDetailDto)
		String loginUrl = "http://localhost:" + this.port + "/api/users/login";
		LoginRequestDto loginRequestDto = new LoginRequestDto("아이디", "pw");
		HttpHeaders loginHeaders = new HttpHeaders();
		loginHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LoginRequestDto> loginRequestEntity = new HttpEntity<>(loginRequestDto, loginHeaders);
		ParameterizedTypeReference<LoginResponseDto> loginResponseType = new ParameterizedTypeReference<>() {
		};
		ResponseEntity<LoginResponseDto> loginResponseEntity = this.restTemplate.exchange(loginUrl,
			HttpMethod.POST,
			loginRequestEntity, loginResponseType);

		// 받아온 토큰 기반 테스트
		String url = "http://localhost:" + this.port + "/api/fromme/1?username=아이디";
		String token = Objects.requireNonNull(loginResponseEntity.getBody()).accessToken();
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
		System.out.println(token);
		ResponseEntity<Void> responseEntity = this.restTemplate.exchange(url, HttpMethod.DELETE, requestEntity,
			Void.class);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void postFromMe() {
		User user = userRepository.save(User.builder()
			.id(1L)
			.username("아이디")
			.nickname("닉네임")
			.content("소개문구")
			.build());

		Post post = postRepository.save(Post.builder()
			.id(1L)
			.content("내용")
			.nickname("닉네임")
			.postType(PostType.FROM_ME)
			.reply("답글")
			.user(user)
			.build());

		List<String> options = new ArrayList<>(List.of("보기1", "보기2", "보기3"));
		String url = "http://localhost:" + this.port + "/api/fromme?username=아이디";
		PostRequestDto postRequestDto = new PostRequestDto("닉네임", "내용", options);
		HttpEntity<PostRequestDto> requestEntity = new HttpEntity<>(postRequestDto);
		ResponseEntity<Void> responseEntity = this.restTemplate.exchange(url, HttpMethod.POST, requestEntity,
			Void.class);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(optionRepository.count()).isEqualTo(3);
	}

	@Test
	void getFromMeFeed() {
		// @AuthenticationPrincipal UserDetailDto
	}

	@Test
	void getFromMe() {
		// @AuthenticationPrincipal UserDetailDto
	}

	@Test
	void postFromMeLikes() {
		// @AuthenticationPrincipal UserDetailDto
	}

	@Test
	void voteFromMe() {
		// @AuthenticationPrincipal UserDetailDto
	}

	@Test
	void getComments() {
		List<User> users = userRepository.saveAll(List.of(
			User.builder()
				.id(1L)
				.username("아이디")
				.nickname("닉네임")
				.content("소개문구")
				.build(),
			User.builder()
				.id(2L)
				.username("아이디2")
				.nickname("닉네임2")
				.content("소개문구2")
				.build()));

		Post post = postRepository.save(Post.builder()
			.id(1L)
			.content("내용")
			.nickname("닉네임")
			.postType(PostType.FROM_ME)
			.user(users.get(0))
			.build());

		List<Comment> comments = commentRepository.saveAll(List.of(
			Comment.builder()
				.id(1L)
				.post(post)
				.user(users.get(0))
				.content("유저1의 댓글")
				.parent(null)
				.build(),
			Comment.builder()
				.id(2L)
				.post(post)
				.user(users.get(1))
				.content("유저2의 댓글")
				.parent(null)
				.build()));

		String url = "http://localhost:" + this.port + "/api/fromme/1/comments";
		PostCommentRequestParam param = new PostCommentRequestParam(1L, 1L, 1);
		HttpEntity<PostCommentRequestParam> requestEntity = new HttpEntity<>(param);
		ParameterizedTypeReference<List<CommentResponseDto>> responseType = new ParameterizedTypeReference<>() {
		};
		ResponseEntity<List<CommentResponseDto>> responseEntity = this.restTemplate.exchange(url, HttpMethod.GET,
			requestEntity, responseType);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
		assertThat(responseEntity.getBody()).isNotNull();
	}

	@Test
	void postComments() {
		User user = userRepository.save(User.builder()
			.id(1L)
			.username("아이디")
			.password("pw")
			.nickname("닉네임")
			.content("소개문구")
			.build());

		Post post = postRepository.save(Post.builder()
			.id(1L)
			.content("내용")
			.nickname("닉네임")
			.postType(PostType.FROM_ME)
			.user(user)
			.build());

		// 로그인 토큰 받기 (@AuthenticationPrincipal UserDetailDto)
		String loginUrl = "http://localhost:" + this.port + "/api/users/login";
		LoginRequestDto loginRequestDto = new LoginRequestDto("아이디", "pw");
		HttpHeaders loginHeaders = new HttpHeaders();
		loginHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<LoginRequestDto> loginRequestEntity = new HttpEntity<>(loginRequestDto, loginHeaders);
		ParameterizedTypeReference<LoginResponseDto> loginResponseType = new ParameterizedTypeReference<>() {
		};
		ResponseEntity<LoginResponseDto> loginResponseEntity = this.restTemplate.exchange(loginUrl,
			HttpMethod.POST,
			loginRequestEntity, loginResponseType);

		// 받아온 토큰 기반 테스트
		String url = "http://localhost:" + this.port + "/api/fromme/1/comments";
		String token = Objects.requireNonNull(loginResponseEntity.getBody()).accessToken();
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		CommentRequestDto comment = new CommentRequestDto(null, "댓글");
		HttpEntity<CommentRequestDto> requestEntity = new HttpEntity<>(comment, headers);
		ParameterizedTypeReference<List<CommentResponseDto>> responseType = new ParameterizedTypeReference<>() {
		};
		ResponseEntity<List<CommentResponseDto>> responseEntity = this.restTemplate.exchange(url, HttpMethod.POST,
			requestEntity, responseType);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
		assertThat(responseEntity.getBody()).isNotNull();
		assertThat(Objects.requireNonNull(responseEntity.getBody()).size()).isEqualTo(1);
	}
}