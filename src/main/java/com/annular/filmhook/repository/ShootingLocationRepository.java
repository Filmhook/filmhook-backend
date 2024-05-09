package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.ShootingLocation;

@Repository
public interface ShootingLocationRepository extends JpaRepository<ShootingLocation, Integer>{

}
