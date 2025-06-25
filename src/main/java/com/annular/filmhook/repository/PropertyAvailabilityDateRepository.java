package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.PropertyAvailabilityDate;

@Repository
public interface PropertyAvailabilityDateRepository extends JpaRepository<PropertyAvailabilityDate, Integer> {
    List<PropertyAvailabilityDate> findByPropertyId(Integer propertyId);
    
    List<PropertyAvailabilityDate> findByPropertyIdIn(List<Integer> propertyIds);
}
