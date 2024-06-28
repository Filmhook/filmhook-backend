package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import com.annular.filmhook.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Industry;

@Repository
public interface IndustryRepository extends JpaRepository<Industry, Integer> {

    @Query("SELECT i FROM Industry i WHERE i.industryName = :industryName")
    Optional<Industry> findByIndustryName(String industryName);

    @Query("SELECT i FROM Industry i WHERE i.industryName = :industryName")
    Industry findByIndustriesName(String industryName);

    @Query("Select i From Industry i Where i.country in (:countryIds) and i.status = true")
    List<Industry> getIndustryByCountryIds(List<Country> countryIds);

    List<Industry> findAllByIndustryName(String upperCase);

}
