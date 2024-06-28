package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.AuditionRoles;

@Repository
public interface AuditionRolesRepository extends JpaRepository<AuditionRoles, Integer> {

    @Modifying
    @Query("DELETE FROM AuditionRoles ar WHERE ar.audition.auditionId = :auditionId")
    void deleteByAuditionId(Integer auditionId);

}
