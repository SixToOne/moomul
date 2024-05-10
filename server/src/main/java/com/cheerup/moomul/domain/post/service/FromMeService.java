package com.cheerup.moomul.domain.post.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cheerup.moomul.domain.member.entity.User;
import com.cheerup.moomul.domain.member.entity.UserDetailDto;
import com.cheerup.moomul.domain.member.repository.UserRepository;
import com.cheerup.moomul.domain.post.dto.CommentRequestDto;
import com.cheerup.moomul.domain.post.dto.CommentResponseDto;
import com.cheerup.moomul.domain.post.dto.PostCommentRequestParam;
import com.cheerup.moomul.domain.post.dto.PostLikeResponseDto;
import com.cheerup.moomul.domain.post.dto.PostRequestDto;
import com.cheerup.moomul.domain.post.dto.PostResponseDto;
import com.cheerup.moomul.domain.post.dto.VoteRequestDto;
import com.cheerup.moomul.domain.post.entity.Comment;
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
	private final CommentRepository commentRepository;

	@Transactional
	public void createFromMe(String username, PostRequestDto postRequestDto) {
		User user = userRepository.findByUsername(username)
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
	public void removeFromMe(String username, Long frommeId) {
		Post post = postRepository.findById(frommeId, PostType.FROM_ME)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_POST_ERROR));
		User loginUser = userRepository.findByUsername(username)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_USER_ERROR));

		if (post.getUser().equals(loginUser)) {
			postRepository.delete(post);
		} else {
			throw new BaseException(ErrorCode.NO_AUTHORITY);
		}
	}

	public PostResponseDto getFromMe(UserDetailDto user, String username, Long frommeId) {
		User findUser = userRepository.findByUsername(username)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_USER_ERROR));

		Post post = postRepository.findById(frommeId, PostType.FROM_ME)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_POST_ERROR));

		Optional<Vote> vote = voteRepository.findByUserIdAndOptionIdIn(findUser.getId(),
			post.getOptionList().stream().map(Option::getId).toList());

		Long voteId = vote.map(Vote::getOption).map(Option::getId).orElse(null);
		boolean liked = false;
		if (user != null) {
			liked = postLikeRepository.existsByUserIdAndPostId(user.Id(), frommeId);
		}
		long voteCnt = voteRepository.countAllByOptionIdIn(
			post.getOptionList().stream().map(Option::getId).toList());

		return PostResponseDto.from(post, voteCnt, voteId, liked);
	}

	public List<PostResponseDto> getFromMeFeed(UserDetailDto user, String username, Pageable pageable) {
		User findUser = userRepository.findByUsername(username)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_USER_ERROR));

		return postRepository.findByUserId(findUser.getId(), PostType.FROM_ME, pageable)
			.stream().map(post -> {
				List<Option> optionList = post.getOptionList() != null ? post.getOptionList() : Collections.emptyList();
				Optional<Vote> vote;
				Long voteId = null;
				boolean liked = false;
				if (user != null) {
					// System.out.println("user.getId(): " + user.Id());
					vote = voteRepository.findByUserIdAndOptionIdIn(user.Id(),
						optionList.stream().map(Option::getId).toList());
					voteId = vote.map(Vote::getOption).map(Option::getId).orElse(null);
					liked = postLikeRepository.existsByUserIdAndPostId(user.Id(), post.getId());
				}
				long voteCnt = voteRepository.countAllByOptionIdIn(
					post.getOptionList().stream().map(Option::getId).toList());

				return PostResponseDto.from(post, voteCnt, voteId, liked);
			}).toList();

	}

	@Transactional
	public PostLikeResponseDto likeFromMe(String username, Long frommeId) {
		Post post = postRepository.findById(frommeId, PostType.FROM_ME)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_POST_ERROR));
		User loginUser = userRepository.findByUsername(username)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_USER_ERROR));

		if (post.getUser().equals(loginUser)) {
			PostLike isLike = postLikeRepository.findByPostIdAndUserId(frommeId, loginUser.getId());

			if (isLike != null) {
				postLikeRepository.deleteById(isLike.getId());
			} else {
				postLikeRepository.save(PostLike.builder().post(post).user(loginUser).build());
			}

			return new PostLikeResponseDto(postLikeRepository.countByPostId(frommeId));
		} else {
			throw new BaseException(ErrorCode.NO_AUTHORITY);
		}
	}

	@Transactional
	public PostResponseDto selectFromMeVote(VoteRequestDto optionId, String username, Long frommeId,
		UserDetailDto user) {
		Post post = postRepository.findById(frommeId, PostType.FROM_ME)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_POST_ERROR));
		User loginUser = userRepository.findById(user.Id())
			.orElseThrow(() -> new BaseException(ErrorCode.NO_USER_ERROR));
		Option options = optionRepository.findById(optionId.voted())
			.orElseThrow(() -> new BaseException(ErrorCode.NO_OPTION_ERROR));
		List<Option> optionList = post.getOptionList();

		if (!optionList.isEmpty()) {
			Vote voted = null;
			for (Option option : optionList) {
				Vote vote = voteRepository.findByOptionIdAndUserId(option.getId(), loginUser.getId()).orElse(null);
				if (vote != null) {
					voted = vote;
				}
			}

			Vote newvote = Vote.builder()
				.user(loginUser)
				.option(options)
				.build();

			if (voted != null) {
				voteRepository.delete(voted);
			}

			if (voted == null || voted.getOption() != newvote.getOption()) {
				voteRepository.save(newvote);
			}
			return getFromMe(user, username, frommeId);
		} else {
			throw new BaseException(ErrorCode.NO_AUTHORITY);
		}
	}

	public List<CommentResponseDto> getComments(Long postId, PostCommentRequestParam param) {
		return commentRepository.findCommentByPostId(postId, param);
	}

	@Transactional
	public void createComments(UserDetailDto user, Long tomeId, CommentRequestDto requestDto) {
		//현재 로그인 user, 게시글 userId, 게시글 Id

		Post post = postRepository.findById(tomeId, PostType.FROM_ME)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_POST_ERROR));
		Comment parent = null;
		if (requestDto.parentId() != null) {
			parent = commentRepository.findById(requestDto.parentId())
				.orElseThrow(() -> new BaseException(ErrorCode.NO_COMMENT_ERROR));
		}
		User loginUser = userRepository.findById(user.Id())
			.orElseThrow(() -> new BaseException(ErrorCode.NO_USER_ERROR));

		Comment comment = Comment.builder().user(loginUser).post(post).content(requestDto.content()).build();

		if (parent != null) {
			comment.addParent(parent);
		}
		commentRepository.save(comment);
	}
}
