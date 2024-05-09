package com.cheerup.moomul.domain.post.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.entity.UserDetailDto;
import com.cheerup.moomul.domain.member.repository.UserRepository;
import com.cheerup.moomul.domain.post.dto.CommentRequestDto;
import com.cheerup.moomul.domain.post.dto.CommentResponseDto;
import com.cheerup.moomul.domain.post.dto.PostCommentRequestParam;
import com.cheerup.moomul.domain.post.dto.PostRequestDto;
import com.cheerup.moomul.domain.post.entity.Comment;
import com.cheerup.moomul.domain.post.entity.Option;
import com.cheerup.moomul.domain.post.entity.Post;
import com.cheerup.moomul.domain.post.entity.PostType;
import com.cheerup.moomul.domain.post.repository.CommentRepository;
import com.cheerup.moomul.domain.post.repository.OptionRepository;
import com.cheerup.moomul.domain.post.repository.PostRepository;

@ExtendWith(MockitoExtension.class)
class ToMeServiceTest {

	@Mock
	CommentRepository commentRepository;

	@Mock
	PostRepository postRepository;

	@Mock
	UserRepository userRepository;

	@Mock
	OptionRepository optionRepository;

	@InjectMocks
	ToMeService toMeService;

	@DisplayName("댓글 테스트")
	@Test
	void getComments() {

		CommentResponseDto commentResponse = new CommentResponseDto(1L, "늘보", "잠잘꺼야", LocalDateTime.now(),
			new ArrayList<>());
		//given
		PostCommentRequestParam param = new PostCommentRequestParam(1L, 1L, 1);
		List<CommentResponseDto> comments = new ArrayList<>();
		comments.add(commentResponse);
		given(commentRepository.findCommentByPostId(1L, param))
			.willReturn(comments);

		//when - stub 상태 
		List<CommentResponseDto> actual = toMeService.getComments(1L, param);
		//then
		assertThat(actual).isNotEmpty();
		BDDMockito.then(commentRepository).should(BDDMockito.times(1)).findCommentByPostId(1L, param);

	}

	@DisplayName("댓글 생성 테스트")
	@Test
	void createComments() {
		// Given
		UserDetailDto userDetailDto = new UserDetailDto(1L,"늘보");
		CommentRequestDto commentResponseDto = new CommentRequestDto(1L, "댓글 달았음");

		User user = User.builder()
			.id(1L)
			.username("늘보")
			.content("잠꾸러기 늘보 채널입니다.")
			.nickname("늘보")
			.build();

		Post post = Post.builder()
			.id(1L)
			.nickname("늘보")
			.user(user)
			.content("[ToMe]너 잠꾸러기니")
			.postType(PostType.TO_ME)
			.build();

		Comment parent = Comment.builder()
			.id(1L)
			.post(post)
			.user(user)
			.content("잠꾸러기 아니야")
			.parent(null)
			.build();

		Comment comment = Comment.builder()
			.id(2L)
			.post(post)
			.user(user)
			.content("잠꾸러기 맞아")
			.parent(parent)
			.build();

		given(postRepository.findById(1L, PostType.TO_ME)).willReturn(Optional.of(post));
		given(commentRepository.findById(1L)).willReturn(Optional.of(comment));
		given(userRepository.findById(1L)).willReturn(Optional.of(user));

		// When
		assertThatCode(
			() -> toMeService.createComments(userDetailDto, 1L, commentResponseDto)).doesNotThrowAnyException();

		// Then
		then(commentRepository).should(times(1)).save(any(Comment.class));
	}

	@DisplayName("투미 생성 테스트")
	@Test
	void createToMe() {

		PostRequestDto postRequestDto = new PostRequestDto("nickname", "content",
			List.of("option1", "option2", "option3"));

		//given
		User user = User.builder()
			.id(1L)
			.username("늘보")
			.content("잠꾸러기 늘보 채널입니다.")
			.nickname("늘보")
			.build();

		Post saved = Post.builder()
			.id(1L)
			.content("content")
			.user(user)
			.nickname("nickname")
			.postType(PostType.TO_ME)
			.build();

		Option option = Option.builder()
			.id(1L)
			.build();

		given(userRepository.findByUsername("늘보")).willReturn(Optional.of(user));
		given(postRepository.save(any(Post.class))).willReturn(saved);
		given(optionRepository.save(any(Option.class))).willReturn(option);

		//when
		assertThatCode(
			() -> toMeService.createToMe("늘보", postRequestDto)).doesNotThrowAnyException();

		//then
		BDDMockito.then(userRepository).should(BDDMockito.times(1)).findByUsername("늘보");
		BDDMockito.then(postRepository).should(BDDMockito.times(1)).save(any(Post.class));
		BDDMockito.then(optionRepository).should(BDDMockito.times(3)).save(any(Option.class));

	}

}
