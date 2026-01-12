package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annular.filmhook.model.AdminRole;

public interface AdminRoleRepository extends JpaRepository<AdminRole, Integer> {
}