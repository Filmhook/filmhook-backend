package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.PlatformPermanentDetail;

@Repository
public interface PlatformPermanentDetailRepository extends JpaRepository<PlatformPermanentDetail, Integer>{

}
