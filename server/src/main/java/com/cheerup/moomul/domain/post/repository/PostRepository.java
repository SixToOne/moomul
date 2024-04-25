package com.cheerup.moomul.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cheerup.moomul.domain.post.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
