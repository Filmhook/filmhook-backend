package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.annular.filmhook.model.AdminUserView;
@Repository
public interface AdminUserViewRepository extends JpaRepository<AdminUserView, Long>{

	
	Optional<AdminUserView> findByUserIdAndCategory(Integer userId, String category);


	@Modifying
	@Query(
	    "UPDATE AdminUserView v " +
	    "SET v.active = false " +
	    "WHERE v.userId = :userId " +
	    "AND v.category = :category"
	)
	void clearView(@Param("userId") Integer userId, @Param("category") String category);
	
	
}
