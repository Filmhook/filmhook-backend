package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.BusinessInformation;

@Repository
public interface BusinessInformationRepository extends JpaRepository<BusinessInformation, Long> {
}
