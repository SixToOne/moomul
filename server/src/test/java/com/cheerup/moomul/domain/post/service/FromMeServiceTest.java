package com.cheerup.moomul.domain.post.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

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
import org.springframework.data.domain.PageImpl;

import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.entity.UserDetailDto;
import com.cheerup.moomul.domain.member.repository.UserRepository;
import com.cheerup.moomul.domain.post.dto.PostRequestDto;
import com.cheerup.moomul.domain.post.dto.PostResponseDto;
import com.cheerup.moomul.domain.post.entity.Option;
import com.cheerup.moomul.domain.post.entity.Post;
import com.cheerup.moomul.domain.post.entity.PostType;
import com.cheerup.moomul.domain.post.entity.Vote;
import com.cheerup.moomul.domain.post.repository.CommentRepository;
import com.cheerup.moomul.domain.post.repository.OptionRepository;
import com.cheerup.moomul.domain.post.repository.PostLikeRepository;
import com.cheerup.moomul.domain.post.repository.PostRepository;
import com.cheerup.moomul.domain.post.repository.VoteRepository;

@ExtendWith(MockitoExtension.class)
class FromMeServiceTest {

	@Mock
	CommentRepository commentRepository;

	@Mock
	PostRepository postRepository;

	@Mock
	UserRepository userRepository;

	@Mock
	OptionRepository optionRepository;

	@Mock
	VoteRepository voteRepository;

	@Mock
	PostLikeRepository postLikeRepository;

	@InjectMocks
	FromMeService fromMeService;

	@DisplayName("FromMe 피드")
	@Test
	void getFromMeFeedTest() {
		//1.
		UserDetailDto userDetailDto = new UserDetailDto(1L,"늘보");
		List<Post> postList = new ArrayList<>();
		postList.add(Post.builder()
			.id(1L)
			.user(User.builder()
				.id(1L)
				.build())
			.optionList(List.of(Option.builder().id(1L).build()))
			.build());

		PageImpl<Post> pageImpl = new PageImpl<>(postList);
		//2.
		Vote vote = Vote.builder()
			.id(1L)
			.user(User.builder()
				.id(1L)
				.build())
			.option(Option.builder()
				.id(1L)
				.build())
			.build();

		List<PostResponseDto> responseDtos = List.of(
			PostResponseDto.from(Post.builder()
				.id(1L)
				.optionList(List.of(Option.builder().id(1L).build()))
				.build(), 1L, 1L, true));

		//given
		given(userRepository.findByUsername("늘보")).willReturn(Optional.of(User.builder().id(1L).build()));
		given(postRepository.findByUserId(1L, PostType.FROM_ME, pageImpl.getPageable())).willReturn(postList);
		given(voteRepository.findByUserIdAndOptionIdIn(1L, List.of(1L))).willReturn(Optional.ofNullable(vote));
		given(postLikeRepository.existsByUserIdAndPostId(1L, 1L)).willReturn(true);
		//when
		assertThatCode(
			() -> fromMeService.getFromMeFeed(userDetailDto, "늘보", pageImpl.getPageable())).doesNotThrowAnyException();
		//then

		BDDMockito.then(postRepository)
			.should(BDDMockito.times(1))
			.findByUserId(1L, PostType.FROM_ME, pageImpl.getPageable());

	}

	@DisplayName("FromMe 생성")
	@Test
	void createFromMeTest() {

		PostRequestDto postRequestDto = new PostRequestDto("nickname", "content",
			List.of("option1", "option2", "option3"));

		User user = User.builder()
			.id(1L)
			.build();

		Post saved = Post.builder()
			.id(1L)
			.user(user)
			.postType(PostType.FROM_ME)
			.build();

		Option option = Option.builder()
			.id(1L)
			.build();

		given(userRepository.findByUsername("늘보")).willReturn(Optional.of(user));
		given(postRepository.save(any(Post.class))).willReturn(saved);
		given(optionRepository.save(any(Option.class))).willReturn(option);

		//when
		assertThatCode(
			() -> fromMeService.createFromMe("늘보", postRequestDto)).doesNotThrowAnyException();

		//then
		BDDMockito.then(userRepository).should(BDDMockito.times(1)).findByUsername("늘보");
		BDDMockito.then(postRepository).should(BDDMockito.times(1)).save(any(Post.class));
		BDDMockito.then(optionRepository).should(BDDMockito.times(3)).save(any(Option.class));
	}
}