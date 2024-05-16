package com.cheerup.moomul.domain.post.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.repository.UserRepository;
import com.cheerup.moomul.domain.post.entity.Post;
import com.cheerup.moomul.domain.post.entity.PostType;

@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {
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

	@BeforeEach
	void setUp() {
		User user = userRepository.save(User.builder()
			.id(1L)
			.username("아이디")
			.nickname("닉네임")
			.content("소개문구")
			.build());

		List<Post> posts = postRepository.saveAll(List.of(
			Post.builder()
				.id(1L)
				.content("내용")
				.nickname("닉네임")
				.postType(PostType.FROM_ME)
				.reply("답글")
				.user(user)
				.build(),
			Post.builder()
				.id(2L)
				.content("내용2")
				.nickname("닉네임")
				.postType(PostType.FROM_ME)
				.user(user)
				.build()));
	}

	@Test
	void findById() {
		Optional<Post> foundPost = postRepository.findById(1L, PostType.FROM_ME);

		assertTrue(foundPost.isPresent());
		assertEquals(foundPost.get().getId(), 1L);
	}

	@Test
	void findByUserId() {
		Pageable pageable = PageRequest.of(0, 10);
		List<Post> list = postRepository.findByUserId(1L, PostType.FROM_ME, pageable);

		assertEquals(list.size(), 2);
		assertThat(list).isNotEmpty();
	}

	@Test
	void findRepliedPost() {
		Pageable pageable = PageRequest.of(0, 10);
		List<Post> repliedList = postRepository.findRepliedPost(1L, PostType.FROM_ME, pageable);

		assertEquals(repliedList.size(), 1);
		assertThat(repliedList).isNotEmpty();
		assertEquals(repliedList.get(0).getReply(), "답글");
	}

	@Test
	void findNotRepliedPost() {
		Pageable pageable = PageRequest.of(0, 10);
		List<Post> notRepliedList = postRepository.findNotRepliedPost(1L, PostType.FROM_ME, pageable);

		assertEquals(notRepliedList.size(), 1);
		assertThat(notRepliedList).isNotEmpty();
	}

}