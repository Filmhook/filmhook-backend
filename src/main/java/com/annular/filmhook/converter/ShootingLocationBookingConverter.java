package com.annular.filmhook.converter;

import com.annular.filmhook.model.BookingStatus;
import com.annular.filmhook.model.ShootingLocationBooking;
import com.annular.filmhook.model.ShootingLocationPropertyDetails;
import com.annular.filmhook.model.User;
import com.annular.filmhook.webmodel.ShootingLocationBookingDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class ShootingLocationBookingConverter {

	public static ShootingLocationBooking toEntity(ShootingLocationBookingDTO dto) {

	    if (dto == null) return null;

	    ShootingLocationBooking booking = new ShootingLocationBooking();

	    // --- INPUT FIELDS ONLY ---
	    booking.setBookingType(dto.getBookingType());
	    booking.setSlotType(dto.getSlotType());
	    booking.setSlotTimings(dto.getSlotTimings());

	    booking.setShootStartDate(dto.getShootStartDate());
	    booking.setShootEndDate(dto.getShootEndDate());

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

                .shootStartDate(b.getShootStartDate())
                .shootEndDate(b.getShootEndDate())

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
}
