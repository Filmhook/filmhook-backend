package com.annular.filmhook.webmodel;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
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
    private String bookingMessage;  
    private String bookingStatus;

}
