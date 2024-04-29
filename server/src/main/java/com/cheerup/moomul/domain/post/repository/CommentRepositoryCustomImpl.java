package com.cheerup.moomul.domain.post.repository;

import static com.cheerup.moomul.domain.post.entity.QComment.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cheerup.moomul.domain.post.dto.CommentResponseDto;
import com.cheerup.moomul.domain.post.dto.PostCommentRequestParam;
import com.cheerup.moomul.domain.post.entity.Comment;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<CommentResponseDto> findCommentByPostId(Long postId, PostCommentRequestParam param) {

		BooleanBuilder where = new BooleanBuilder();
		where.and(comment.post.id.eq(postId));
		// where.and(comment.parent.post.id.eq(param.primaryOffset()));
		// where.and(comment.post.id.eq(param.primaryOffset())).and(comment.parent.id.gt(param.subOffset()));

		List<Comment> comments = queryFactory.select(comment)
			.from(comment)
			.innerJoin(comment).on(comment.id.eq(comment.parent.id))
			.where(where)
			// .limit(param.limit())
			.orderBy(
				comment.parent.id.asc().nullsFirst(),
				comment.createdAt.asc()
			).fetch();

		List<CommentResponseDto> commentResponseDtos = new ArrayList<>();
		Map<Long, CommentResponseDto> commentResponseDtoMap = new HashMap<>();

		for (Comment c : comments) {
			CommentResponseDto commentResponseDto = new CommentResponseDto(
				c.getId(),
				c.getUser().getNickname(),
				c.getContent(),
				c.getCreatedAt()
			);

			commentResponseDtoMap.put(commentResponseDto.id(), commentResponseDto);

			// 부모 댓글이 null이 아닌 경우 부모 댓글의 자식  	댓글 리스트에 현재 댓글을 추가
			if (c.getParent() != null) {
				commentResponseDtoMap.get(c.getParent().getId()).replies().add(commentResponseDto);

			} else {
				// 부모 댓글이 null인 경우 최상위 댓글 리스트에 추가
				commentResponseDtos.add(commentResponseDto);
			}
		}
		return commentResponseDtos;
	}
}
