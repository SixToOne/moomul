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
import com.cheerup.moomul.domain.post.entity.Post;
import com.cheerup.moomul.domain.post.entity.PostLike;
import com.cheerup.moomul.domain.post.entity.PostType;

@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostLikeRepositoryTest {
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
	private PostLikeRepository postLikeRepository;

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
				.build(),
			Post.builder()
				.id(2L)
				.content("내용2")
				.nickname("닉네임")
				.postType(PostType.FROM_ME)
				.user(users.get(0))
				.build()));

		PostLike postlike = postLikeRepository.save(
			PostLike.builder()
				.id(1L)
				.post(posts.get(0))
				.user(users.get(0))
				.build());
	}

	@Test
	void existsByUserIdAndPostId() {
		boolean exist = postLikeRepository.existsByUserIdAndPostId(1L, 1L);
		boolean exist2 = postLikeRepository.existsByUserIdAndPostId(1L, 2L);
		boolean exist3 = postLikeRepository.existsByUserIdAndPostId(2L, 1L);

		assertTrue(exist);
		assertFalse(exist2);
		assertFalse(exist3);
	}

	@Test
	void findByPostIdAndUserId() {
		PostLike like = postLikeRepository.findByPostIdAndUserId(1L, 1L);

		assertNotNull(like);
	}

	@Test
	void countByPostId() {
		long count = postLikeRepository.countByPostId(1L);
		long count2 = postLikeRepository.countByPostId(2L);

		assertEquals(count, 1L);
		assertEquals(count2, 0);
	}
}
