package com.cheerup.moomul.domain.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cheerup.moomul.domain.post.entity.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {

	Optional<Vote> findByUserIdAndOptionIdIn(Long userId, List<Long> optionIds);

	Vote findByUserIdAndOptionId(Long userId, Long voted);

	Optional<Vote> findByOptionIdAndUserId(Long optionId, Long userId);
}
