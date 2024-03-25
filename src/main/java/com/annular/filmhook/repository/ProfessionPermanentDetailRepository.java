package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.ProfessionPermanentDetail;

@Repository
public interface ProfessionPermanentDetailRepository extends JpaRepository<ProfessionPermanentDetail, Integer>{

}
