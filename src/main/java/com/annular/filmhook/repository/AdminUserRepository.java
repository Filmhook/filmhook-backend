package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annular.filmhook.model.AdminUser;

public interface AdminUserRepository extends JpaRepository<AdminUser, Integer> {
	
    Optional<AdminUser> findByEmail(String email);
}
