package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.LocationType;


@Repository
public interface LocationTypeRepository extends JpaRepository<LocationType, Long>{

}
