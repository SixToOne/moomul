package com.cheerup.moomul.domain.post.service;

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
import com.cheerup.moomul.domain.post.dto.ReplyRequestDto;
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
public class ToMeService {
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;
	private final OptionRepository optionRepository;
	private final VoteRepository voteRepository;
	private final PostLikeRepository postLikeRepository;

	public List<CommentResponseDto> getComments(Long postId, PostCommentRequestParam param) {
		return commentRepository.findCommentByPostId(postId, param);
	}

	@Transactional
	public void createComments(UserDetailDto user, Long tomeId, CommentRequestDto requestDto) {
		//현재 로그인 user, 게시글 userId, 게시글 Id

		Post post = postRepository.findById(tomeId, PostType.TO_ME)
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

	@Transactional
	public void createToMe(Long userId, PostRequestDto postRequestDto) {
		User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.NO_USER_ERROR));

		Post saved = postRepository.save(Post.builder()
			.content(postRequestDto.content())
			.user(user)
			.nickname(postRequestDto.nickname())
			.postType(PostType.TO_ME)
			.build());

		for (String option : postRequestDto.options()) {
			optionRepository.save(Option.builder().post(saved).content(option).build());
		}
	}

	@Transactional
	public void removeToMe(Long userId, Long tomeId) {
		Post post = postRepository.findById(tomeId, PostType.TO_ME)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_POST_ERROR));
		User loginUser = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.NO_USER_ERROR));

		if (post.getUser().equals(loginUser)) {
			postRepository.delete(post);
		} else {
			throw new BaseException(ErrorCode.NO_AUTHORITY);
		}
	}

	@Transactional
	public void createReplies(ReplyRequestDto reply, Long userId, Long tomeId, UserDetailDto user, Pageable pageable) {
		Post post = postRepository.findById(tomeId, PostType.TO_ME)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_POST_ERROR));
		User loginUser = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.NO_USER_ERROR));

		if (post.getUser().equals(loginUser)) {
			post.addReply(reply.reply());
			getNotRepliedToMe(user, userId, pageable);
			getRepliedToMe(user, userId, pageable);
		} else {
			throw new BaseException(ErrorCode.NO_AUTHORITY);
		}
	}

	@Transactional
	public PostLikeResponseDto likeToMe(UserDetailDto user, Long userId, Long tomeId) {
		Post post = postRepository.findById(tomeId, PostType.TO_ME)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_POST_ERROR));
		if (!post.getUser().getId().equals(userId)) {
			throw new BaseException(ErrorCode.NO_POST_ERROR);
		}

		User loginUser = userRepository.findById(user.Id())
			.orElseThrow(() -> new BaseException(ErrorCode.NO_AUTHORITY));
		PostLike isLike = postLikeRepository.findByPostIdAndUserId(tomeId, loginUser.getId());

		if (isLike != null) {
			postLikeRepository.deleteById(isLike.getId());
		} else {
			postLikeRepository.save(PostLike.builder().post(post).user(loginUser).build());
		}

		return new PostLikeResponseDto(postLikeRepository.countByPostId(tomeId));
	}

	@Transactional
	public PostResponseDto selectToMeVote(VoteRequestDto optionId, Long userId, Long tomeId,
		UserDetailDto user) {
		Post post = postRepository.findById(tomeId, PostType.TO_ME)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_POST_ERROR));
		User loginUser = userRepository.findById(user.Id())
			.orElseThrow(() -> new BaseException(ErrorCode.NO_USER_ERROR));
		Option options = optionRepository.findById(optionId.voted())
			.orElseThrow(() -> new BaseException(ErrorCode.NO_OPTION_ERROR));
		List<Option> optionList = post.getOptionList();

		if (post.getUser().equals(loginUser) && !optionList.isEmpty()) {
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

			return getToMe(user, userId, tomeId);
		} else {
			throw new BaseException(ErrorCode.NO_AUTHORITY);
		}
	}

	public PostResponseDto getToMe(UserDetailDto user, Long userId, Long tomeId) {
		Post post = postRepository.findById(tomeId, PostType.TO_ME)
			.orElseThrow(() -> new BaseException(ErrorCode.NO_POST_ERROR));

		Optional<Vote> vote = voteRepository.findByUserIdAndOptionIdIn(userId,
			post.getOptionList().stream().map(Option::getId).toList());

		Long voteId = vote.map(Vote::getOption).map(Option::getId).orElse(null);
		boolean liked = false;
		if (user != null) {
			liked = postLikeRepository.existsByUserIdAndPostId(user.Id(), tomeId);
		}
		long voteCnt = voteRepository.countAllByOptionIdIn(
			post.getOptionList().stream().map(Option::getId).toList());

		return PostResponseDto.from(post, voteCnt, voteId, liked);
	}

	public List<PostResponseDto> getRepliedToMe(UserDetailDto user, Long userId, Pageable pageable) {
		return postRepository.findRepliedPost(userId, PostType.TO_ME, pageable).stream().map(post -> {
			Optional<Vote> vote;
			Long voteId = null;
			boolean liked = false;
			if (user != null) {
				vote = voteRepository.findByUserIdAndOptionIdIn(user.Id(),
					post.getOptionList().stream().map(Option::getId).toList());
				voteId = vote.map(Vote::getOption).map(Option::getId).orElse(null);
				liked = postLikeRepository.existsByUserIdAndPostId(user.Id(), post.getId());
			}

			long voteCnt = voteRepository.countAllByOptionIdIn(
				post.getOptionList().stream().map(Option::getId).toList());

			return PostResponseDto.from(post, voteCnt, voteId, liked);
		}).toList();
	}

	public List<PostResponseDto> getNotRepliedToMe(UserDetailDto user, Long userId, Pageable pageable) {
		return postRepository.findNotRepliedPost(userId, PostType.TO_ME, pageable).stream().map(post -> {
			System.out.println(post.getId());
			Optional<Vote> vote;
			Long voteId = null;
			boolean liked = false;
			if (user != null) {
				vote = voteRepository.findByUserIdAndOptionIdIn(user.Id(),
					post.getOptionList().stream().map(Option::getId).toList());
				voteId = vote.map(Vote::getOption).map(Option::getId).orElse(null);
				liked = postLikeRepository.existsByUserIdAndPostId(user.Id(), post.getId());
			}
			long voteCnt = voteRepository.countAllByOptionIdIn(
				post.getOptionList().stream().map(Option::getId).toList());

			return PostResponseDto.from(post, voteCnt, voteId, liked);
		}).toList();
	}
}