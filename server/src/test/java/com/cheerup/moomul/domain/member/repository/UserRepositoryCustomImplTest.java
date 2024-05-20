package com.cheerup.moomul.domain.member.repository;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.cheerup.moomul.domain.member.entity.ProfileDto;
import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.post.entity.Post;
import com.cheerup.moomul.domain.post.entity.PostType;
import com.cheerup.moomul.domain.post.repository.PostRepository;

@Testcontainers
@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryCustomImplTest {

	@Container
	public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.3");

	@Autowired
	PostRepository postRepository;

	@Autowired
	UserRepository userRepository;

	@BeforeEach
	void setUp() {
		User user = User.builder()
			.id(1L)
			.username("늘보")
			.password("늘보")
			.content("늘보")
			.nickname("늘보")
			.build();

		userRepository.save(user);

		List<Post> posts = List.of(Post.builder()
				.user(user)
				.nickname("늘보")
				.postType(PostType.TO_ME)
				.content("늘보")
				.build(),

			Post.builder()
				.user(user)
				.nickname("늘보")
				.postType(PostType.FROM_ME)
				.content("늘보")
				.build()
		);

		postRepository.saveAll(posts);

	}

	@Test
	void findProfileById() {

		// When
		ProfileDto profile = userRepository.findProfileById(1L);

		// Then
		assertThat(profile).isNotNull();
		assertThat(profile.nickname()).isEqualTo("늘보");
		assertThat(profile.content()).isEqualTo("늘보");
		assertThat(profile.toMe()).isEqualTo(1L);
		assertThat(profile.fromMe()).isEqualTo(1L);
	}

}