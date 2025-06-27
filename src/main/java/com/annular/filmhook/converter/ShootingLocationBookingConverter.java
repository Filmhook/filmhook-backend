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

    public static ShootingLocationBooking toEntity(ShootingLocationBookingDTO dto, User client, ShootingLocationPropertyDetails property) {
        Double pricePerDay = property.getPriceCustomerPay();
        long days = ChronoUnit.DAYS.between(dto.getShootStartDate(), dto.getShootEndDate()) + 1;
        double totalAmount = pricePerDay * days;

        return ShootingLocationBooking.builder()
        		
                .shootStartDate(dto.getShootStartDate())
                .shootEndDate(dto.getShootEndDate())
                .bookingDate(LocalDate.now())
                .status(BookingStatus.PENDING)
                .createdBy(dto.getClientId())
                .client(client)
                .property(property)
                .pricePerDay(pricePerDay)
                .totalAmount(totalAmount)
                .bookingMessage(dto.getBookingMessage())
                .build();
    }

    public static ShootingLocationBookingDTO toDTO(ShootingLocationBooking booking) {
        return ShootingLocationBookingDTO.builder()
        		.bookingId(booking.getId())
                .propertyId(booking.getProperty().getId())
                .clientId(booking.getClient().getUserId())
                .createdBy(booking.getClient().getUserId())
                .shootStartDate(booking.getShootStartDate())
                .shootEndDate(booking.getShootEndDate())
                .bookingMessage(booking.getBookingMessage())
                .pricePerDay(booking.getPricePerDay())
                .totalAmount(booking.getTotalAmount())
                .build();
    }
}
