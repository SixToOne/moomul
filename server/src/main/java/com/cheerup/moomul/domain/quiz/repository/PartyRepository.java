package com.cheerup.moomul.domain.quiz.repository;

import org.springframework.data.repository.CrudRepository;

import com.cheerup.moomul.domain.quiz.entity.Party;
public interface PartyRepository extends CrudRepository<Party, String> {

}
