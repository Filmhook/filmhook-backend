package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.AdminUserViewLog;

@Repository
public interface AdminUserViewLogRepository extends JpaRepository<AdminUserViewLog, Integer> {


	List<AdminUserViewLog> findByUserIdAndCategory(
			Integer userId,
			String category
			);
	
	boolean existsByAdminIdAndUserIdAndCategory(
		    Integer adminId,
		    Integer userId,
		    String category
		);
	
	
}
