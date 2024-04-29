package com.cheerup.moomul.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cheerup.moomul.domain.post.entity.Vote;

public interface PostLikeRepository extends JpaRepository<Vote, Long> {
}