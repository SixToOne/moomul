package com.cheerup.moomul.domain.member.repository;

import org.springframework.data.repository.CrudRepository;

import com.cheerup.moomul.domain.member.entity.Today;

public interface TodayRepository extends CrudRepository<Today,String> {
}
