package com.cheerup.moomul.domain.member.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "today")
public class Today {
	@Id
	private String username;
	private int today;
}
