package com.annular.filmhook.model;

import javax.persistence.*;

import com.annular.filmhook.util.LocalDateListConverter;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "shooting_property_availability_dates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShootingPropertyAvailabilityDates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate startDate;

    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private ShootingLocationPropertyDetails property;
    
    @Convert(converter = LocalDateListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<LocalDate> pausedDates;
}
