package com.cheerup.moomul.domain.post.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum WritingType {
	NORMAL("Normal"), VOTE("Vote");

	private final String type;

	@JsonCreator
	public static WritingType from(String value) {
		for (WritingType status : WritingType.values()) {
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
