package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.annular.filmhook.model.ShootingLocationImages;


public interface ShootingLocationImagesRepository extends JpaRepository<ShootingLocationImages, Integer> {
    Optional<ShootingLocationImages> findByIndustryMediaid(Integer industryMediaid);
}
