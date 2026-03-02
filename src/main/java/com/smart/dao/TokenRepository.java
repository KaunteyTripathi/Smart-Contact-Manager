package com.smart.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.entities.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {

	Token findByToken(String token);
}