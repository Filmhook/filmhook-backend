package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.annular.filmhook.model.IndustryTemporaryDetails;

@Repository
public interface IndustryTemporaryDetailRepository extends JpaRepository<IndustryTemporaryDetails, Integer> {

	
	List<IndustryTemporaryDetails> findByUserId(Integer userId);

	@Transactional
	@Modifying
	@Query("DELETE FROM IndustryTemporaryDetails i WHERE i.userId = :userId")
	void deleteByUserId(Integer userId);

}
