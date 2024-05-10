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
import com.cheerup.moomul.domain.post.dto.VoteRequestDto;
import com.cheerup.moomul.domain.post.entity.Option;
import com.cheerup.moomul.domain.post.entity.Post;
import com.cheerup.moomul.domain.post.entity.PostLike;
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
		UserDetailDto userDetailDto = new UserDetailDto(1L, "늘보");
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

	@DisplayName("FromMe 삭제 테스트")
	@Test
	void deleteFromMeTest() {
		User user = User.builder()
			.id(1L)
			.username("아이디")
			.nickname("닉네임")
			.content("소개문구")
			.build();

		Post post = Post.builder()
			.id(1L)
			.content("내용")
			.nickname("닉네임")
			.postType(PostType.FROM_ME)
			.user(user)
			.build();

		given(userRepository.findByUsername("아이디")).willReturn(Optional.of(user));
		given(postRepository.findById(1L, PostType.FROM_ME)).willReturn(Optional.of(post));

		assertThatCode(() -> fromMeService.removeFromMe("아이디", 1L)).doesNotThrowAnyException();

		verify(postRepository).delete(post);
	}

	@DisplayName("FromMe 좋아요 테스트")
	@Test
	void likeFromMeTest() {
		User loginUser = User.builder()
			.id(1L)
			.username("아이디")
			.nickname("닉네임")
			.content("소개문구")
			.build();

		Post post = Post.builder()
			.id(1L)
			.content("내용")
			.nickname("닉네임")
			.postType(PostType.FROM_ME)
			.user(loginUser)
			.postLikeList(new ArrayList<>())
			.build();

		PostLike isLike = PostLike.builder()
			.id(1L)
			.post(post)
			.user(loginUser)
			.build();

		// 기존 data가 없는 경우 추가
		given(userRepository.findByUsername("아이디")).willReturn(Optional.of(loginUser));
		given(postRepository.findById(1L, PostType.FROM_ME)).willReturn(Optional.of(post));

		assertThatCode(() -> fromMeService.likeFromMe("아이디", 1L)).doesNotThrowAnyException();

		BDDMockito.then(userRepository).should(BDDMockito.times(1)).findByUsername("아이디");
		BDDMockito.then(postRepository).should(BDDMockito.times(1)).findById(1L, PostType.FROM_ME);
		then(postLikeRepository).should(BDDMockito.times(1)).save(any(PostLike.class));

		// 기존 data가 있는 경우 삭제
		given(userRepository.findByUsername("아이디")).willReturn(Optional.of(loginUser));
		given(postRepository.findById(1L, PostType.FROM_ME)).willReturn(Optional.of(post));
		given(postLikeRepository.findByPostIdAndUserId(1L, 1L)).willReturn(isLike);

		assertThatCode(() -> fromMeService.likeFromMe("아이디", 1L)).doesNotThrowAnyException();

		then(postLikeRepository).should(BDDMockito.times(1)).deleteById(isLike.getId());
	}

	@DisplayName("FromMe 투표 테스트")
	@Test
	void voteFromMeTest() {
		UserDetailDto userDetailDto = new UserDetailDto(1L, "아이디");
		VoteRequestDto voteRequestDto = new VoteRequestDto(1L);

		User user = User.builder()
			.id(1L)
			.username("아이디")
			.nickname("닉네임")
			.content("소개문구")
			.build();

		Post post = Post.builder()
			.id(1L)
			.content("내용")
			.nickname("닉네임")
			.postType(PostType.FROM_ME)
			.user(user)
			.optionList(new ArrayList<>())
			.build();

		for (long i = 0; i < 3; i++) {
			Option option = Option.builder()
				.id(i + 1)
				.content("보기 " + (i + 1))
				.post(post)
				.voteCnt(0L)
				.build();
			post.getOptionList().add(option);
		}

		// 추가
		given(userRepository.findByUsername("아이디")).willReturn(Optional.of(user));
		given(postRepository.findById(1L, PostType.FROM_ME)).willReturn(Optional.of(post));
		given(optionRepository.findById(1L)).willReturn(
			Optional.of(post.getOptionList().get(voteRequestDto.voted().intValue() - 1)));

		assertThatCode(
			() -> fromMeService.selectFromMeVote(voteRequestDto, "아이디", 1L, userDetailDto)).doesNotThrowAnyException();

		BDDMockito.then(userRepository).should(BDDMockito.times(2)).findByUsername("아이디");
		BDDMockito.then(postRepository).should(BDDMockito.times(2)).findById(1L, PostType.FROM_ME);
		then(voteRepository).should(BDDMockito.times(1)).save(any(Vote.class));

		// 삭제
		Vote voted = Vote.builder()
			.id(1L)
			.option(post.getOptionList().get(voteRequestDto.voted().intValue() - 1))
			.user(user)
			.build();

		given(userRepository.findByUsername("아이디")).willReturn(Optional.of(user));
		given(postRepository.findById(1L, PostType.FROM_ME)).willReturn(Optional.of(post));
		given(optionRepository.findById(1L)).willReturn(
			Optional.of(post.getOptionList().get(voteRequestDto.voted().intValue() - 1)));
		given(voteRepository.findByOptionIdAndUserId(1L, 1L)).willReturn(Optional.of(voted));

		assertThatCode(
			() -> fromMeService.selectFromMeVote(voteRequestDto, "아이디", 1L, userDetailDto)).doesNotThrowAnyException();

		then(voteRepository).should(BDDMockito.times(1)).delete(voted);
	}

}