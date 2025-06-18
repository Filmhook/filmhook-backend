package com.annular.filmhook.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String businessLocation;
    private String businessType;
    private String businessName;
    private String panOrGstin;

    @Embedded
    private BusinessAddress businessAddress;
}
