package com.annular.filmhook.repository;

import com.annular.filmhook.model.MarketPlaceProducts;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketPlaceProductRepository extends JpaRepository<MarketPlaceProducts, Integer> {
	   List<MarketPlaceProducts> findBySubCategory_Id(Integer subCategoryId);
}
