package com.cheerup.moomul.domain.post.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

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
class VoteRepositoryTest {
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
				.build(),
			User.builder()
				.id(2L)
				.username("아이디2")
				.nickname("닉네임2")
				.content("소개문구2")
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
	void findByUserIdAndOptionIdIn() {
		List<Long> optionIds = List.of(1L, 2L, 3L);
		List<Long> noOptionIds = List.of(4L, 5L);
		Optional<Vote> optionalVote = voteRepository.findByUserIdAndOptionIdIn(1L, optionIds);
		Optional<Vote> optionalVote2 = voteRepository.findByUserIdAndOptionIdIn(1L, noOptionIds);
		Optional<Vote> optionalVote3 = voteRepository.findByUserIdAndOptionIdIn(2L, optionIds);

		assertTrue(optionalVote.isPresent());
		assertFalse(optionalVote2.isPresent());
		assertFalse(optionalVote3.isPresent());

		assertEquals(optionalVote.get().getUser().getId(), 1L);
		assertEquals(optionalVote.get().getOption().getId(), optionIds.get(0));
	}

	@Test
	void findByUserIdAndOptionId() {
		Vote vote = voteRepository.findByUserIdAndOptionId(1L, 1L);
		Vote notVote = voteRepository.findByUserIdAndOptionId(1L, 2L);
		Vote notVote2 = voteRepository.findByUserIdAndOptionId(2L, 1L);

		assertNotNull(vote);
		assertNull(notVote);
		assertNull(notVote2);

		assertEquals(vote.getUser().getId(), 1L);
		assertEquals(vote.getOption().getId(), 1L);
	}

	@Test
	void findByOptionIdAndUserId() {
		Optional<Vote> optionalVote = voteRepository.findByOptionIdAndUserId(1L, 1L);
		Optional<Vote> optionalVote2 = voteRepository.findByOptionIdAndUserId(2L, 1L);
		Optional<Vote> optionalVote3 = voteRepository.findByOptionIdAndUserId(1L, 2L);

		assertTrue(optionalVote.isPresent());
		assertFalse(optionalVote2.isPresent());
		assertFalse(optionalVote3.isPresent());

		assertEquals(optionalVote.get().getUser().getId(), 1L);
		assertEquals(optionalVote.get().getOption().getId(), 1L);
	}

	@Test
	void countAllByOptionIdIn() {
		List<Long> optionIds = List.of(1L, 2L, 3L);
		List<Long> noOptionIds = List.of(4L, 5L);
		long count = voteRepository.countAllByOptionIdIn(optionIds);
		long noCount = voteRepository.countAllByOptionIdIn(noOptionIds);

		assertEquals(count, 1L);
		assertEquals(noCount, 0L);
	}
}
