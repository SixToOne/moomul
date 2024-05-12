package com.cheerup.moomul.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cheerup.moomul.domain.post.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
}
