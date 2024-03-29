package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.annular.filmhook.model.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

	Optional<RefreshToken> findByToken(String token);

	@Query("select rt from RefreshToken rt where rt.userId=:userId")
	Optional<RefreshToken> findByUserId(Integer userId);
}
