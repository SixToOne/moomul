package com.cheerup.moomul.domain.post.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.cheerup.moomul.domain.post.entity.Post;

public record PostResponseDto(
	Long id,
	String nickname,
	String content,
	List<OptionResponseDto> options,
	Long voted,
	String reply,
	Long likeCnt,
	Long commentCnt,
	boolean liked,
	LocalDateTime createdAt
) {
	public static PostResponseDto from(Post p, Long voted, boolean liked) {
		return new PostResponseDto(
			p.getId(),
			p.getNickname(),
			p.getContent(),
			p.getOptionList().stream().map(OptionResponseDto::from).toList(),
			voted,
			p.getReply(),
			p.getLikeCnt(),
			p.getCommentCnt(),
			liked,
			p.getCreatedAt()
		);
	}
}
