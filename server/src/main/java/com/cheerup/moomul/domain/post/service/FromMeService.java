package com.cheerup.moomul.domain.post.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.entity.UserDetailDto;
import com.cheerup.moomul.domain.member.repository.UserRepository;
import com.cheerup.moomul.domain.post.dto.PostLikeResponseDto;
import com.cheerup.moomul.domain.post.dto.PostRequestDto;
import com.cheerup.moomul.domain.post.dto.PostResponseDto;
import com.cheerup.moomul.domain.post.dto.VoteRequestDto;
import com.cheerup.moomul.domain.post.entity.Option;
import com.cheerup.moomul.domain.post.entity.Post;
import com.cheerup.moomul.domain.post.entity.PostLike;
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
			.orElseThrow(() -> new BaseException(ErrorCode.NO_USER_ERROR));

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

	@Transactional
	public void removeFromMe(Long userId, Long frommeId) {
		Post post = postRepository.findById(frommeId, PostType.FROM_ME)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_POST_ERROR));
		User loginUser = userRepository.findById(userId)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_USER_ERROR));

		if (post.getUser().equals(loginUser)) {
			postRepository.delete(post);
		} else {
			throw new BaseException(ErrorCode.NO_AUTHORITY);
		}
	}

	public PostResponseDto getFromMe(UserDetailDto user, Long userId, Long frommeId) {
		Post post = postRepository.findById(frommeId, PostType.FROM_ME)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_POST_ERROR));

		Optional<Vote> vote = voteRepository.findByUserIdAndOptionIdIn(userId,
			post.getOptionList().stream().map(Option::getId).toList());

		Long voteId = vote.map(Vote::getOption).map(Option::getId).orElse(null);
		boolean liked = false;
		if (user != null) {
			liked = postLikeRepository.existsByUserIdAndPostId(user.Id(), frommeId);
		}

		return PostResponseDto.from(post, voteId, liked);
	}

	public List<PostResponseDto> getFromMeFeed(UserDetailDto user, Long userId, Pageable pageable) {
		return postRepository.findByUserId(userId, PostType.FROM_ME, pageable)
			.stream().map(post -> {
				Optional<Vote> vote;
				Long voteId = null;
				boolean liked = false;
				if (user != null) {
					System.out.println("user.getId(): " + user.Id());
					vote = voteRepository.findByUserIdAndOptionIdIn(user.Id(),
						post.getOptionList().stream().map(Option::getId).toList());
					voteId = vote.map(Vote::getOption).map(Option::getId).orElse(null);
					liked = postLikeRepository.existsByUserIdAndPostId(user.Id(), post.getId());
				}
				return PostResponseDto.from(post, voteId, liked);
			}).toList();

	}

	@Transactional
	public PostLikeResponseDto likeFromMe(UserDetailDto user, Long userId, Long frommeId) {
		Post post = postRepository.findById(frommeId, PostType.FROM_ME)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_POST_ERROR));
		if (!post.getUser().getId().equals(userId)) {
			throw new BaseException(ErrorCode.NO_POST_ERROR);
		}

		User loginUser = userRepository.findById(user.Id())
			.orElseThrow(() -> new BaseException(ErrorCode.NO_AUTHORITY));
		PostLike isLike = postLikeRepository.findByPostIdAndUserId(frommeId, loginUser.getId());

		if (isLike != null) {
			postLikeRepository.deleteById(isLike.getId());
		} else {
			postLikeRepository.save(PostLike.builder()
				.post(post)
				.user(loginUser)
				.build());
		}

		return new PostLikeResponseDto(postLikeRepository.countByPostId(frommeId));
	}

	@Transactional
	public void selectFromMeVote(VoteRequestDto optionId, Long userId, Long frommeId, UserDetailDto user) {
		Post post = postRepository.findById(frommeId, PostType.FROM_ME)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_POST_ERROR));
		User loginUser = userRepository.findById(user.Id())
			.orElseThrow(() -> new BaseException(ErrorCode.NO_USER_ERROR));
		Option options = optionRepository.findById(optionId.voted())
			.orElseThrow(() -> new BaseException(ErrorCode.NO_OPTION_ERROR));

		if (!post.getOptionList().isEmpty()) {
			Vote isVoted = voteRepository.findByUserIdAndOptionId(loginUser.getId(), optionId.voted());

			if (isVoted != null) {
				voteRepository.deleteById(isVoted.getId());
			} else {
				voteRepository.save(Vote.builder().user(loginUser).option(options).build());
			}
			getFromMe(user, userId, frommeId);
		} else {
			throw new BaseException(ErrorCode.NO_OPTION_ERROR);
		}
	}
}
