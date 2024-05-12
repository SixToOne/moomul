package com.cheerup.moomul.domain.member.repository;

import com.cheerup.moomul.domain.member.entity.ProfileDto;

public interface UserRepositoryCustom {
	ProfileDto findProfileById(Long userId);
}
