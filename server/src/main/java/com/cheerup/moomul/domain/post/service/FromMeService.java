package com.cheerup.moomul.domain.post.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.repository.UserRepository;
import com.cheerup.moomul.domain.post.dto.PostRequestDto;
import com.cheerup.moomul.domain.post.dto.PostResponseDto;
import com.cheerup.moomul.domain.post.entity.Option;
import com.cheerup.moomul.domain.post.entity.Post;
import com.cheerup.moomul.domain.post.entity.PostType;
import com.cheerup.moomul.domain.post.entity.Vote;
import com.cheerup.moomul.domain.post.repository.OptionRepository;
import com.cheerup.moomul.domain.post.repository.PostLikeRepository;
import com.cheerup.moomul.domain.post.repository.PostRepository;
import com.cheerup.moomul.domain.post.repository.VoteRepository;
import com.cheerup.moomul.global.response.BaseException;
import com.cheerup.moomul.global.response.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FromMeService {

	private final PostRepository postRepository;
	private final OptionRepository optionRepository;
	private final PostLikeRepository postLikeRepository;
	private final VoteRepository voteRepository;
	private final UserRepository userRepository;

	@Transactional
	public void createFromMe(Long userId, PostRequestDto postRequestDto) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

		Post saved = postRepository.save(Post.builder()
			.content(postRequestDto.content())
			.user(user)
			.nickname(postRequestDto.nickname())
			.postType(PostType.FROM_ME)
			.build());

		for (String option : postRequestDto.options()) {
			optionRepository.save(Option.builder()
				.post(saved)
				.content(option)
				.build());
		}
	}

	public PostResponseDto getFromMe(Long userId, Long frommeId) {
		Post post = postRepository.findById(frommeId, PostType.FROM_ME)
			.orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));

		Optional<Vote> vote = voteRepository.findByUserIdAndOptionIdIn(userId,
			post.getOptionList().stream().map(Option::getId).toList());

		Long voteId = vote.map(Vote::getOption).map(Option::getId).orElse(null);

		boolean liked = postLikeRepository.existsByUserIdAndPostId(userId, frommeId);

		return PostResponseDto.from(post, voteId, liked);
	}

	public List<PostResponseDto> getFromMeFeed(Long userId, Pageable pageable) {
		return postRepository.findByUserId(userId, PostType.FROM_ME, pageable)
			.stream().map(post -> {
				Optional<Vote> vote = voteRepository.findByUserIdAndOptionIdIn(userId,
					post.getOptionList().stream().map(Option::getId).toList());
				Long voteId = vote.map(Vote::getOption).map(Option::getId).orElse(null);
				boolean liked = postLikeRepository.existsByUserIdAndPostId(userId, post.getId());
				return PostResponseDto.from(post, voteId, liked);
			}).toList();
	}
}
