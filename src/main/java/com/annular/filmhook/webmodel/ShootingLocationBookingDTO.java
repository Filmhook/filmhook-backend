package com.annular.filmhook.webmodel;

import java.time.LocalDate;

import com.annular.filmhook.model.BookingStatus;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShootingLocationBookingDTO {
private Integer bookingId;
    private Integer propertyId;          
    private Integer clientId;          
    private Integer createdBy;        

    private LocalDate shootStartDate;  
    private LocalDate shootEndDate;    
    private Double pricePerDay;
    private Double totalAmount;
    private Double baseAmount;
    private Double gstAmount;
    private String bookingMessage;  
    private String bookingStatus;

}


