package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.PlatformDetails;

@Repository
public interface PlatformDetailRepository extends JpaRepository<PlatformDetails, Integer>{

	List<PlatformDetails> findByIntegerTemporaryDetailId(Integer itId);

}
