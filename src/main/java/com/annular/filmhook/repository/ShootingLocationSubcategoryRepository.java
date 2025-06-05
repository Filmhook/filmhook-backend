package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.annular.filmhook.model.ShootingLocationSubcategory;

public interface ShootingLocationSubcategoryRepository extends JpaRepository <ShootingLocationSubcategory, Integer> {

	@Query("SELECT s FROM ShootingLocationSubcategory s WHERE s.category.id = :categoryId")
	List<ShootingLocationSubcategory> findByCategoryId(@Param("categoryId") Integer categoryId);


}
