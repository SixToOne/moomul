package com.cheerup.moomul.domain.post.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.repository.UserRepository;
import com.cheerup.moomul.domain.post.entity.Option;
import com.cheerup.moomul.domain.post.entity.Post;
import com.cheerup.moomul.domain.post.entity.PostType;
import com.cheerup.moomul.domain.post.entity.Vote;

@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OptionRepositoryTest {
	@Container
	private static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0.3");

	@Test
	void checkConnect() {
		assertTrue(mySQLContainer.isCreated());
		assertTrue(mySQLContainer.isRunning());
	}

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private VoteRepository voteRepository;

	@Autowired
	private OptionRepository optionRepository;

	@BeforeEach
	void setUp() {
		List<User> users = userRepository.saveAll(List.of(
			User.builder()
				.id(1L)
				.username("아이디")
				.nickname("닉네임")
				.content("소개문구")
				.build()));

		List<Post> posts = postRepository.saveAll(List.of(
			Post.builder()
				.id(1L)
				.content("내용")
				.nickname("닉네임")
				.postType(PostType.FROM_ME)
				.reply("답글")
				.user(users.get(0))
				.build()));

		List<Option> options = optionRepository.saveAll(List.of(
			Option.builder()
				.id(1L)
				.content("보기1")
				.post(posts.get(0))
				.voteCnt(1L)
				.build(),
			Option.builder()
				.id(2L)
				.content("보기2")
				.post(posts.get(0))
				.voteCnt(0L)
				.build(),
			Option.builder()
				.id(3L)
				.content("보기3")
				.post(posts.get(0))
				.voteCnt(0L)
				.build()));

		Vote vote = voteRepository.save(
			Vote.builder()
				.id(1L)
				.option(options.get(0))
				.user(users.get(0))
				.build());
	}

	@Test
	void findByPostId() {
		List<Option> optionList = optionRepository.findByPostId(1L);

		assertFalse(optionList.isEmpty());
		assertEquals(optionList.size(), 3);
	}
}
