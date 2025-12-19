
package com.annular.filmhook.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.Payments;
import com.annular.filmhook.model.SlotType;
import com.annular.filmhook.model.User;
import com.annular.filmhook.webmodel.BookingWithPropertyDTO;
import com.annular.filmhook.webmodel.PropertyAvailabilityDTO;
import com.annular.filmhook.webmodel.ShootingLocationBookingDTO;
import com.annular.filmhook.webmodel.ShootingLocationCategoryDTO;
import com.annular.filmhook.webmodel.ShootingLocationFileInputModel;
import com.annular.filmhook.webmodel.ShootingLocationPropertyDetailsDTO;
import com.annular.filmhook.webmodel.ShootingLocationPropertyReviewDTO;
import com.annular.filmhook.webmodel.ShootingLocationPropertyReviewResponseDTO;
import com.annular.filmhook.webmodel.ShootingLocationSubcategoryDTO;
import com.annular.filmhook.webmodel.ShootingLocationTypeDTO;
import com.annular.filmhook.webmodel.ShootingPaymentModel;


public interface ShootingLocationService {

	List<ShootingLocationTypeDTO> getAllTypes();
	List<ShootingLocationCategoryDTO> getCategoriesByTypeId(Integer typeId);
	List<ShootingLocationSubcategoryDTO> getSubcategoriesByCategoryId(Integer categoryId);
	void saveSelection(Long subcategoryId, Boolean entire, Boolean single);
	Response savePropertyDetails(ShootingLocationPropertyDetailsDTO dto, ShootingLocationFileInputModel inputFile);
	List<ShootingLocationPropertyDetailsDTO> getAllProperties(Integer userId);
	Response getPropertiesByUserId(Integer userId);
	Response deletePropertyById(Integer id);
	ShootingLocationPropertyDetailsDTO updatePropertyDetails(Integer id, ShootingLocationPropertyDetailsDTO dto, ShootingLocationFileInputModel inputFile);
	String toggleLike(Integer propertyId, Integer userId);
	Long countLikes(Integer propertyId);
	List<ShootingLocationPropertyDetailsDTO> getPropertiesByIndustryIds(List<Integer> industryIds, Integer userId);
	public ShootingLocationPropertyReviewDTO saveReview(
	        Integer propertyId,
	        Integer userId,
	        int rating,
	        String reviewText,
	        List<MultipartFile> files);
	double getAverageRating(Integer propertyId);
	public ShootingLocationPropertyReviewResponseDTO getReviewsByPropertyId(Integer propertyId, Integer userId);
//	PropertyAvailabilityDTO saveAvailability(PropertyAvailabilityDTO dto);
//	List<PropertyAvailabilityDTO> getAvailabilityByPropertyId(Integer propertyId);
//	void updateAvailabilityDates(Integer propertyId, List<PropertyAvailabilityDTO> availabilityList);
	List<ShootingLocationPropertyDetailsDTO> getPropertiesLikedByUser(Integer userId);
	ShootingLocationPropertyDetailsDTO getPropertyByBookingId(Integer bookingId);
	
	String deleteReview(Integer reviewId, Integer userId);
	ShootingLocationPropertyReviewDTO updateReview(Integer reviewId, Integer propertyId, Integer userId, int rating,
			String reviewText, List<MultipartFile> files, List<Integer> deletedFileIds);
	List<LocalDate> getAvailableDatesForProperty( Integer propertyId, SlotType requestedSlot) ;
	ShootingLocationBookingDTO createBooking(ShootingLocationBookingDTO dto);
	
	
	Payments createShootingPayment(ShootingPaymentModel model);
	public ResponseEntity<Response> handleShootingLocationPaymentSuccess(String txnid);
	ResponseEntity<?> handleShootingLocationPaymentFailed(String txnid, String reason);
	
	List<ShootingLocationPropertyDetailsDTO> getPropertiesByIndustryIdsAndDates(
			Integer industryId,
			Integer userId,
			LocalDate startDate,
			LocalDate endDate, SlotType slotType);
	ShootingLocationPropertyReviewDTO replyToReview(Integer reviewId, Integer ownerUserId, String replyText);
	ShootingLocationPropertyReviewDTO deleteReply(Integer reviewId, Integer ownerUserId);
	ResponseEntity<?> saveAdminPropertyRating(ShootingLocationPropertyDetailsDTO request);
	List<BookingWithPropertyDTO> getBookingHistoryByClientId(Integer clientId);
	List<ShootingLocationPropertyDetailsDTO> getPropertiesSorted(String sortBy, String order, String propertyType, String priceType);
}
