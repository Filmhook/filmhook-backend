package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.annular.filmhook.model.MarketPlaceSubCategories;

public interface MarketPlaceSubCategoryRepository extends JpaRepository<MarketPlaceSubCategories, Integer> {
	 
	 List<MarketPlaceSubCategories> findByCategory_Id(Integer categoryId);

}