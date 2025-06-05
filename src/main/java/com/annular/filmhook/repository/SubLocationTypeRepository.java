package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annular.filmhook.model.SubLocationType;

public interface SubLocationTypeRepository extends JpaRepository<SubLocationType, Long>{
	List<SubLocationType> findByLocationTypeId(Long locationTypeId);
}
