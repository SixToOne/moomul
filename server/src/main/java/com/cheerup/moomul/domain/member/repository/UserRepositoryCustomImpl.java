package com.cheerup.moomul.domain.member.repository;

import static com.cheerup.moomul.domain.member.entity.QUser.*;
import static com.cheerup.moomul.domain.post.entity.QPost.*;

import org.springframework.stereotype.Repository;

import com.cheerup.moomul.domain.member.entity.ProfileDto;
import com.cheerup.moomul.domain.post.entity.PostType;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	@Override
	public ProfileDto findProfileById(Long userId) {

		return queryFactory
			.select(Projections.constructor(ProfileDto.class,
				user.nickname,
				user.content,
				user.image,
				JPAExpressions.select(post.id.count())
					.from(post)
					.where(post.postType.eq(PostType.TO_ME).and(post.user.id.eq(userId))),
				JPAExpressions.select(post.id.count())
					.from(post)
					.where(post.postType.eq(PostType.FROM_ME).and(post.user.id.eq(userId)))
				))
			.from(user)
			.where(user.id.eq(userId))
			.fetchOne();
	}
}
