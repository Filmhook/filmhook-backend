package com.annular.filmhook.service;

import java.util.List;

import com.annular.filmhook.model.MarketPlaceCategories;
import com.annular.filmhook.model.MarketPlaceSubCategories;

import com.annular.filmhook.webmodel.MarketPlaceCategoryDTO;
import com.annular.filmhook.webmodel.MarketPlaceProductDTO;
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
	    List<MarketPlaceProductDTO> getAllProducts();
	    MarketPlaceProductDTO getProductById(Integer id);
	    void deleteProduct(Integer id);
	     List<MarketPlaceProductDTO> getProductsBySubCategoryId(Integer subCategoryId);
	     void updateProduct(Integer productId, MarketPlaceProductDTO dto, SellerFileInputModel mediaFiles);
	     List<MarketPlaceProductDTO> getProductsByUserId(Long userId);
}
