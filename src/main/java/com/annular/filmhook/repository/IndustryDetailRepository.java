package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.IndustryDetails;

@Repository
public interface IndustryDetailRepository extends JpaRepository<IndustryDetails, Integer> {

}
