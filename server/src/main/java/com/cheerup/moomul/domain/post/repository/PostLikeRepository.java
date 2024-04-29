package com.cheerup.moomul.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cheerup.moomul.domain.post.entity.PostLike;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
	boolean existsByUserIdAndPostId(Long userId, Long postId);
}
