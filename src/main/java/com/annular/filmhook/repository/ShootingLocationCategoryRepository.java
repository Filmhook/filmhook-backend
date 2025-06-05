package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.annular.filmhook.model.ShootingLocationCategory;

public interface ShootingLocationCategoryRepository extends JpaRepository<ShootingLocationCategory, Integer> {

	@Query("SELECT c FROM ShootingLocationCategory c WHERE c.type.id = :typeId")
	List<ShootingLocationCategory> getCategoriesByTypeId(@Param("typeId") Integer typeId);
}
