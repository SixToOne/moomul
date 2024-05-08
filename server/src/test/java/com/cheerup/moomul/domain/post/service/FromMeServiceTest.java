package com.cheerup.moomul.domain.post.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

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
import com.cheerup.moomul.domain.member.repository.UserRepository;
import com.cheerup.moomul.domain.post.dto.PostRequestDto;
import com.cheerup.moomul.domain.post.entity.Option;
import com.cheerup.moomul.domain.post.entity.Post;
import com.cheerup.moomul.domain.post.entity.PostType;
import com.cheerup.moomul.domain.post.repository.CommentRepository;
import com.cheerup.moomul.domain.post.repository.OptionRepository;
import com.cheerup.moomul.domain.post.repository.PostRepository;

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

	@InjectMocks
	FromMeService fromMeService;

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

		given(userRepository.findById(1L)).willReturn(Optional.of(user));
		given(postRepository.save(any(Post.class))).willReturn(saved);
		given(optionRepository.save(any(Option.class))).willReturn(option);

		//when
		assertThatCode(
			() -> fromMeService.createFromMe(1L, postRequestDto)).doesNotThrowAnyException();

		//then
		BDDMockito.then(userRepository).should(BDDMockito.times(1)).findById(1L);
		BDDMockito.then(postRepository).should(BDDMockito.times(1)).save(any(Post.class));
		BDDMockito.then(optionRepository).should(BDDMockito.times(3)).save(any(Option.class));
	}
}