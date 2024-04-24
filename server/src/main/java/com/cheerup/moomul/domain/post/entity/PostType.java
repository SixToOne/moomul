package com.cheerup.moomul.domain.post.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PostType {
	TO_ME("ToMe"), FROM_ME("FromMe");

	private final String type;

	@JsonCreator
	public static PostType from(String value) {
		for (PostType status : PostType.values()) {
			if (status.getType().equals(value)) {
				return status;
			}
		}
		return null;
	}

	@JsonValue
	public String getType() {
		return type;
	}
}
