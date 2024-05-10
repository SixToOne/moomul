package com.cheerup.moomul.domain.quiz.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.cheerup.moomul.domain.quiz.dto.SubmitRequestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "submit")
public class SubmitInfo {

	@Id
	private String username;
	@Setter
	private SubmitRequestDto hostSubmit;
	@Builder.Default
	private List<SubmitRequestDto> submits = new ArrayList<>();

	public void addSubmit(SubmitRequestDto submit) {
		submits.add(submit);
	}

	public int getSubmitSize() {
		return submits.size();
	}

	public boolean isHostSubmitted() {
		return hostSubmit == null;
	}

}
