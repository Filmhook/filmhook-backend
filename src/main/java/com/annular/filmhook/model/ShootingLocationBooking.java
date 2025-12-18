package com.annular.filmhook.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.annular.filmhook.util.LocalDateListConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shooting_location_bookings")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShootingLocationBooking {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @ManyToOne
	    @JoinColumn(name = "property_id", nullable = false)
	    private ShootingLocationPropertyDetails property;

	    @ManyToOne
	    @JoinColumn(name = "client_id", nullable = false)
	    private User client;

//	    // Booking Dates
//	    private LocalDate shootStartDate;
//	    private LocalDate shootEndDate;
//	    
	    @Convert(converter = LocalDateListConverter.class)
		@Column(columnDefinition = "TEXT")
		private List<LocalDate> bookingDates; 
	    
	    private Integer totalDays;
	    
	    @Enumerated(EnumType.STRING)
	    private PropertyBookingType bookingType;  

	    @Enumerated(EnumType.STRING)
	    private SlotType slotType;              

	    private String slotTimings;    

	    private Double pricePerDay;             
	    private Double subtotal;                    

	    private Double discountPercent;         
	    private Double discountAmount;           

	    private Double amountAfterDiscount;    

	    private Double gstPercent;              
	    private Double gstAmount;               

	    private Double netAmount;               

	    private String bookingMessage;

	    @Enumerated(EnumType.STRING)
	    private BookingStatus status;

	    @CreationTimestamp
	    private LocalDateTime createdAt;

	    private LocalDateTime updatedAt;
	
  
}
