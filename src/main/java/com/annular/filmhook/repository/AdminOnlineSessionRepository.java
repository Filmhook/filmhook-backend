package com.annular.filmhook.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.annular.filmhook.model.AdminOnlineSession;

public interface AdminOnlineSessionRepository extends JpaRepository<AdminOnlineSession, Integer>{
	
	  // Fetch ONLY the last active (open) session
    @Query("SELECT s FROM AdminOnlineSession s " +
           "WHERE s.adminId = :adminId AND s.logoutTime IS NULL " +
           "ORDER BY s.loginTime DESC")
    List<AdminOnlineSession> findOpenSession(
            @Param("adminId") Integer adminId,
            Pageable pageable
    );


    // Fetch today's sessions for work hours calculation
    @Query("SELECT s FROM AdminOnlineSession s " +
           "WHERE s.adminId = :adminId " +
           "AND s.loginTime BETWEEN :startOfDay AND :endOfDay")
    List<AdminOnlineSession> findTodaySessions(
            @Param("adminId") Integer adminId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
	
	@Query("SELECT s FROM AdminOnlineSession s WHERE s.adminId = :adminId ORDER BY s.id DESC")
	AdminOnlineSession findLastActiveSession(@Param("adminId") Integer adminId);


}
