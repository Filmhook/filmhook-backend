package com.annular.filmhook.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.annular.filmhook.webmodel.ShootingLocationCategoryDTO;
import com.annular.filmhook.webmodel.ShootingLocationFileInputModel;
import com.annular.filmhook.webmodel.ShootingLocationPropertyDetailsDTO;
import com.annular.filmhook.webmodel.ShootingLocationSubcategoryDTO;
import com.annular.filmhook.webmodel.ShootingLocationTypeDTO;


public interface ShootingLocationService {

	List<ShootingLocationTypeDTO> getAllTypes();
    List<ShootingLocationCategoryDTO> getCategoriesByTypeId(Integer typeId);
    List<ShootingLocationSubcategoryDTO> getSubcategoriesByCategoryId(Integer categoryId);
    void saveSelection(Long subcategoryId, Boolean entire, Boolean single);
    ShootingLocationPropertyDetailsDTO savePropertyDetails(ShootingLocationPropertyDetailsDTO dto, ShootingLocationFileInputModel inputFile);
    List<ShootingLocationPropertyDetailsDTO> getAllProperties();
    List<ShootingLocationPropertyDetailsDTO> getPropertiesByUserId(Integer userId);
    void deletePropertyById(Long id);
    ShootingLocationPropertyDetailsDTO updateProperty(Long id, ShootingLocationPropertyDetailsDTO dto) ;

   
}
