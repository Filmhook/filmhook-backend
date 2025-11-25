package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.annular.filmhook.model.ShootingLocationSubcategorySelection;

public interface ShootingLocationSubcategorySelectionRepository extends JpaRepository<ShootingLocationSubcategorySelection, Long> {

	
	
	@Query("SELECT s FROM ShootingLocationSubcategorySelection s WHERE s.entirePropertyDiscount20Percent = true OR s.singlePropertyDiscount20Percent = true")
    List<ShootingLocationSubcategorySelection> findAllWithAnyDiscountEnabled();
}
