package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.ShootingLocation;

@Repository
public interface ShootingLocationRepository extends JpaRepository<ShootingLocation, Integer> {

    @Query("select s from ShootingLocation s where s.shootingLocationName = :searchKey")
    List<ShootingLocation> findBySearchKey(String searchKey);
   
    @Query("SELECT s FROM ShootingLocation s WHERE s.shootingLocationIsactive = true AND s.userId = :id")
	List<ShootingLocation> findbyUserId(Integer id);


}
