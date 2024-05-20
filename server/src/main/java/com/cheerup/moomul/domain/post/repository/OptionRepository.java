package com.cheerup.moomul.domain.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cheerup.moomul.domain.post.entity.Option;

public interface OptionRepository extends JpaRepository<Option, Long> {
	List<Option> findByPostId(Long postId);
}
