package com.cheerup.moomul.domain.post.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

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

import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.repository.UserRepository;
import com.cheerup.moomul.domain.post.dto.CommentResponseDto;
import com.cheerup.moomul.domain.post.dto.PostCommentRequestParam;
import com.cheerup.moomul.domain.post.entity.Comment;
import com.cheerup.moomul.domain.post.entity.Post;
import com.cheerup.moomul.domain.post.entity.PostType;

@Testcontainers
// @DataJdbcTest
@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest {

	@Container
	public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.3");

	@Autowired
	PostRepository postRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	CommentRepository commentRepository;

	@Test
	void connectionEstablished() {
		assertThat(mysqlContainer.isCreated()).isTrue();
		assertThat(mysqlContainer.isRunning()).isTrue();
	}

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

		// Comment comment = Comment.builder()
		// 	.id(2L)
		// 	.post(posts.get(0))
		// 	.user(user)
		// 	.content("잠꾸러기 맞아")
		// 	.parent(parent)
		// 	.build();
		//
		// commentRepository.save(comment);
	}

	@Transactional
	@Test
	void findCommentByPostId() {
		PostCommentRequestParam param = new PostCommentRequestParam(1L, 1L, 1);
		List<CommentResponseDto> comment = commentRepository.findCommentByPostId(1L, param);
		System.out.println(comment.get(0).toString());
		assertThat(comment.size()).isEqualTo(2);
		assertThat(comment).isNotEmpty();
	}
}