package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annular.filmhook.model.MarketPlaceSubCategoryFields;

public interface MarketPlaceSubCategoryFiledsRepository extends JpaRepository<MarketPlaceSubCategoryFields, Integer> {
	 List<MarketPlaceSubCategoryFields> findBySubCategories_Id(Integer subCategoryId);
	
}
