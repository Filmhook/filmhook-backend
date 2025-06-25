package com.annular.filmhook.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
@NoArgsConstructor                    
@AllArgsConstructor
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

    @Column(name = "created_by")
    private Integer createdBy;

    private LocalDate bookingDate;
    private LocalDate shootStartDate;
    private LocalDate shootEndDate;

    private Double pricePerDay;
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private String bookingMessage;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and setters...
}
