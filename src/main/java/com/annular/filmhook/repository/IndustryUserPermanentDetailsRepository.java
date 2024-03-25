package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.IndustryUserPermanentDetails;

@Repository
public interface IndustryUserPermanentDetailsRepository extends JpaRepository<IndustryUserPermanentDetails,Integer>{

}