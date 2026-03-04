package com.annular.filmhook.converter;


import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.ShootingLocationBooking;
import com.annular.filmhook.model.ShootingLocationPropertyDetails;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.ShootingLocationBookingDTO;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ShootingLocationBookingConverter {
@Autowired
	   MediaFilesService mediaFilesService;
	
	public static ShootingLocationBooking toEntity(ShootingLocationBookingDTO dto) {

		if (dto == null) return null;

		ShootingLocationBooking booking = new ShootingLocationBooking();

		// --- INPUT FIELDS ONLY ---
		booking.setBookingType(dto.getBookingType());
		booking.setSlotType(dto.getSlotType());
		booking.setSlotTimings(dto.getSlotTimings());

		//	    booking.setShootStartDate(dto.getShootStartDate());
		//	    booking.setShootEndDate(dto.getShootEndDate());

		booking.setBookingDates(dto.getBookingDates());
		booking.setBookingMessage(dto.getBookingMessage());

		return booking;
	}

	public static ShootingLocationBookingDTO toDTO(ShootingLocationBooking b) {

		if (b == null) return null;

		return ShootingLocationBookingDTO.builder()
				.bookingId(b.getId())
				.propertyId(b.getProperty().getId())
				.clientId(b.getClient().getUserId())
				.createdBy(b.getProperty().getCreatedBy())

				.bookingType(b.getBookingType())
				.slotType(b.getSlotType())
				.slotTimings(b.getSlotTimings())
				.bookingDates(b.getBookingDates())
				.confirmedBookingDates(b.getConfirmedBookingDates())
				.modificationRequested(b.getModificationRequested())
				.totalDays(b.getTotalDays())

				// ------- PRICE BREAKDOWN -------
				.pricePerDay(b.getPricePerDay())
				.subtotal(b.getSubtotal())
				.discountPercent(b.getDiscountPercent())
				.discountAmount(b.getDiscountAmount())
				.amountAfterDiscount(b.getAmountAfterDiscount())
				.gstPercent(b.getGstPercent())
				.gstAmount(b.getGstAmount())
				.netAmount(b.getNetAmount())

				.bookingMessage(b.getBookingMessage())
				.bookingStatus(b.getStatus().name())
				.build();
	}
	
	
	public ShootingLocationBookingDTO convertToDTO(ShootingLocationBooking booking) {

	    ShootingLocationPropertyDetails property = booking.getProperty();


	    Integer propertyId = property.getId();

	 
		// 🔥 Fetch images
	    List<FileOutputWebModel> imageUrls =
	            mediaFilesService.getAllMediaFilesByCategoryAndRefId(
	                    MediaFileCategory.shootingLocationImage,
	                    propertyId
	            );

	 
	    return ShootingLocationBookingDTO.builder()
	            .bookingId(booking.getId())
	            .propertyId(property.getId())
	            .propertyName(property.getPropertyName())
	            .imageUrls(imageUrls)
	            .clientId(booking.getClient().getUserId())
	            .bookingType(booking.getBookingType())
	            .slotType(booking.getSlotType())
	            .slotTimings(booking.getSlotTimings())
	            .bookingDates(booking.getBookingDates())
	            .totalDays(booking.getTotalDays())
	            .pricePerDay(booking.getPricePerDay())
	            .subtotal(booking.getSubtotal())
	            .discountPercent(booking.getDiscountPercent())
	            .discountAmount(booking.getDiscountAmount())
	            .amountAfterDiscount(booking.getAmountAfterDiscount())
	            .gstPercent(booking.getGstPercent())
	            .gstAmount(booking.getGstAmount())
	            .netAmount(booking.getNetAmount())
	            .bookingStatus(booking.getStatus().name())
	            .bookingMessage(booking.getBookingMessage())
	            .PropertyCode(property.getPropertyCode())
	            .bookingCode(booking.getBookingCode())
	            .confirmedBookingDates(booking.getConfirmedBookingDates())
	            .modificationRequested(booking.getModificationRequested())
	            .shootVerified(booking.getShootVerified())
	            .build();
	}
	
	
	


}
