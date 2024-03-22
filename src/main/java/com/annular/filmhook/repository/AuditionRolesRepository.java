package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.AuditionRoles;

@Repository
public interface AuditionRolesRepository extends JpaRepository<AuditionRoles, Integer> {

}
