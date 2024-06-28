package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.AuditionDetails;

@Repository
public interface AuditionDetailsRepository extends JpaRepository<AuditionDetails, Integer> {

}
