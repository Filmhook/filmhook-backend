package com.annular.filmhook.service;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.model.MarketPlaceCategories;
import com.annular.filmhook.model.MarketPlaceSubCategories;

import com.annular.filmhook.webmodel.MarketPlaceCategoryDTO;
import com.annular.filmhook.webmodel.MarketPlaceLikesDTO;
import com.annular.filmhook.webmodel.MarketPlaceProductDTO;
import com.annular.filmhook.webmodel.MarketPlaceProductReviewDTO;
import com.annular.filmhook.webmodel.MarketPlaceSubCategoryDTO;
import com.annular.filmhook.webmodel.MarketPlaceSubCategoryFieldDTO;
import com.annular.filmhook.webmodel.SellerFileInputModel;

public interface MarketPlaceProductService {
	  List<MarketPlaceCategoryDTO> getAllCategories();
	    MarketPlaceCategoryDTO getCategoryById(Integer id);
	    MarketPlaceCategoryDTO saveCategory(MarketPlaceCategoryDTO categoryDTO);
	  
	    List<MarketPlaceSubCategoryDTO> getAllSubCategories(Integer categoryId);
	    MarketPlaceSubCategoryDTO saveSubCategory(MarketPlaceSubCategoryDTO dto);
	    
	    MarketPlaceSubCategoryFieldDTO saveSubCategoryField(MarketPlaceSubCategoryFieldDTO dto);
	    List<MarketPlaceSubCategoryFieldDTO> getFieldsBySubCategoryId(Integer subCategoryId);
	    
	    MarketPlaceProductDTO saveProduct(MarketPlaceProductDTO dto, SellerFileInputModel mediaFiles) ;
	    List<MarketPlaceProductDTO> getAllProducts(Integer currentUserId);
	    MarketPlaceProductDTO getProductById(Integer id);
	    void deleteProduct(Integer id);
	    List<MarketPlaceProductDTO> getProductsBySubCategoryId(Integer subCategoryId, Integer currentUserId);
	     void updateProduct(Integer productId, MarketPlaceProductDTO dto, SellerFileInputModel mediaFiles);
	     List<MarketPlaceProductDTO> getProductsByUserId(Long userId);

	     void saveReview(MarketPlaceProductReviewDTO dto);
	     void deleteReview(Integer reviewId);
	     String saveLike(MarketPlaceLikesDTO dto);
	     List<MarketPlaceProductDTO> getWishlistProducts(Integer userId);

}
