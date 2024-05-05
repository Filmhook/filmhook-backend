package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.annular.filmhook.model.PlatformDetails;

@Repository
public interface PlatformDetailRepository extends JpaRepository<PlatformDetails, Integer>{

	List<PlatformDetails> findByIntegerTemporaryDetailId(Integer itId);

	@Transactional
    @Modifying
    @Query("DELETE FROM PlatformDetails pd WHERE pd.integerTemporaryDetailId = :temporaryId")
	void deleteByIntegerTemporaryDetailId(Integer temporaryId);

	@Transactional
	@Modifying
	@Query("DELETE FROM PlatformDetails i WHERE i.userId = :userId")
	void deleteByUserId(Integer userId);

}
