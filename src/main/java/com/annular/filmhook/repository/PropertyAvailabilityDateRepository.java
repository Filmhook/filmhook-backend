package com.annular.filmhook.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.annular.filmhook.model.PropertyAvailabilityDate;

@Repository
public interface PropertyAvailabilityDateRepository extends JpaRepository<PropertyAvailabilityDate, Integer> {
    List<PropertyAvailabilityDate> findByPropertyId(Integer propertyId);
    
    List<PropertyAvailabilityDate> findByPropertyIdIn(List<Integer> propertyIds);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM PropertyAvailabilityDate p WHERE p.property.id = :propertyId")
    void deleteByPropertyId(Integer propertyId);
    
    List<PropertyAvailabilityDate> findByProperty_Id(Integer propertyId);
    
//    @Query("SELECT DISTINCT a.property.id FROM PropertyAvailabilityDate a " +
//    	       "WHERE a.startDate <= :filterEndDate " +
//    	       "AND a.endDate >= :filterStartDate")
//    	List<Integer> findAvailablePropertyIds(
//    	        LocalDate filterStartDate,
//    	        LocalDate filterEndDate
//    	);



}
