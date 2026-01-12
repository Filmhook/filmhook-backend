package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annular.filmhook.model.AdminRefreshToken;


public interface AdminRefreshTokenRepository extends JpaRepository<AdminRefreshToken, Integer> {

}
