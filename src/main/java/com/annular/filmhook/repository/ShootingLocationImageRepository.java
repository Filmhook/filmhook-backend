package com.annular.filmhook.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.annular.filmhook.model.ShootingLocationImages;
import com.annular.filmhook.model.ShootingLocationPropertyDetails;

public interface ShootingLocationImageRepository extends JpaRepository<ShootingLocationImages, Integer>{
	 void deleteAllByProperty(ShootingLocationPropertyDetails property);
	 List<ShootingLocationImages> findByProperty(ShootingLocationPropertyDetails property);


}
