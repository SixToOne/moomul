package com.cheerup.moomul.domain.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cheerup.moomul.domain.post.entity.Post;
import com.cheerup.moomul.domain.post.entity.PostType;

public interface PostRepository extends JpaRepository<Post, Long> {
	@Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.optionList WHERE p.user.id = :userId and p.postType = :postType ORDER BY p.createdAt DESC")
	List<Post> findByUserId(Long userId, PostType postType, Pageable pageable);

	@Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.optionList WHERE p.id = :postId and p.postType = :postType")
	Optional<Post> findById(Long postId, PostType postType);
}
