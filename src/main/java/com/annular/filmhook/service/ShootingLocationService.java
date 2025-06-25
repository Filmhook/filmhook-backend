
package com.annular.filmhook.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.annular.filmhook.webmodel.PropertyAvailabilityDTO;
import com.annular.filmhook.webmodel.ShootingLocationCategoryDTO;
import com.annular.filmhook.webmodel.ShootingLocationFileInputModel;
import com.annular.filmhook.webmodel.ShootingLocationPropertyDetailsDTO;
import com.annular.filmhook.webmodel.ShootingLocationPropertyReviewDTO;
import com.annular.filmhook.webmodel.ShootingLocationSubcategoryDTO;
import com.annular.filmhook.webmodel.ShootingLocationTypeDTO;


public interface ShootingLocationService {

	List<ShootingLocationTypeDTO> getAllTypes();
    List<ShootingLocationCategoryDTO> getCategoriesByTypeId(Integer typeId);
    List<ShootingLocationSubcategoryDTO> getSubcategoriesByCategoryId(Integer categoryId);
    void saveSelection(Long subcategoryId, Boolean entire, Boolean single);
    ShootingLocationPropertyDetailsDTO savePropertyDetails(ShootingLocationPropertyDetailsDTO dto, ShootingLocationFileInputModel inputFile);
    List<ShootingLocationPropertyDetailsDTO> getAllProperties(Integer userId);
    List<ShootingLocationPropertyDetailsDTO> getPropertiesByUserId(Integer userId);
    void deletePropertyById(Integer id);
    ShootingLocationPropertyDetailsDTO updateProperty(Integer id, ShootingLocationPropertyDetailsDTO dto) ;
    String toggleLike(Integer propertyId, Integer userId);
    Long countLikes(Integer propertyId);
    List<ShootingLocationPropertyDetailsDTO> getPropertiesByIndustryIds(List<Integer> industryIds, Integer userId);
    void saveReview(Integer propertyId, Integer userId, int rating, String reviewText);
    double getAverageRating(Integer propertyId);
    List<ShootingLocationPropertyReviewDTO> getReviewsByPropertyId(Integer propertyId);
    
    PropertyAvailabilityDTO saveAvailability(PropertyAvailabilityDTO dto);
    List<PropertyAvailabilityDTO> getAvailabilityByPropertyId(Integer propertyId);

    
}
