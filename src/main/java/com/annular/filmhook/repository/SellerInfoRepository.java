package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.annular.filmhook.model.SellerInfo;

public interface SellerInfoRepository extends JpaRepository<SellerInfo, Long> {
	
	   @Query("SELECT s FROM SellerInfo s WHERE s.user.userId = :userId")
	    Optional<SellerInfo> findSellerInfoByUserId(@Param("userId") Integer userId);

	   @Query("SELECT s FROM SellerInfo s WHERE s.user.userId = :userId")
	    Optional<SellerInfo> findByUserId(@Param("userId") Integer userId);


}