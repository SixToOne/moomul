package com.cheerup.moomul.domain.quiz.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.cheerup.moomul.domain.quiz.dto.SubmitRequestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "submit")
public class SubmitInfo {

	@Id
	private Long userId;
	private List<SubmitRequestDto> submits;

}
