package com.annular.filmhook.webmodel;

import java.time.LocalDate;

import com.annular.filmhook.model.BookingStatus;
import com.annular.filmhook.model.PropertyBookingType;
import com.annular.filmhook.model.SlotType;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShootingLocationBookingDTO {
    private Integer propertyId;
    private Integer clientId;
    private PropertyBookingType bookingType;
    private SlotType slotType;
    private String slotTimings;
    private LocalDate shootStartDate;
    private LocalDate shootEndDate;
    private String bookingMessage;
    private Integer bookingId;
    private Integer createdBy;
    private Integer totalDays;
    private Double pricePerDay;
    private Double subtotal;            
    private Double discountPercent;
    private Double discountAmount;
    private Double amountAfterDiscount;
    private Double gstPercent;	
    private Double gstAmount;
    private Double netAmount;
    private String bookingStatus;


}


