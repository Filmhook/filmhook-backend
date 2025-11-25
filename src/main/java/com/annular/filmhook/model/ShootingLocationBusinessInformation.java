package com.annular.filmhook.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shooting_location_business_information")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShootingLocationBusinessInformation {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 private String businessLocation;
 private String businessType;
 private String businessName;
 private String panOrGSTNumber;
 private String location;
 private String addressLine1;
 private String addressLine2;
 private String addressLine3;
 private String state;
 private String postalCode;
 @OneToOne(mappedBy = "businessInformation")
 private ShootingLocationPropertyDetails propertyDetails;

}
