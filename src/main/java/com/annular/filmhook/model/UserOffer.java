package com.annular.filmhook.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_offer")
public class UserOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType; // e.g. AUDITION only, or PROMOTION

    private Double customRate;       // e.g. 10 for audition,
    private Double discountPercentage;

    private LocalDateTime validTill;
    private Boolean active;
}

