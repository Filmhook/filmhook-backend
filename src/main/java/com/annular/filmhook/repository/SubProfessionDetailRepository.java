package com.annular.filmhook.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.annular.filmhook.model.SubProfessionDetails;

@Repository
public interface SubProfessionDetailRepository extends JpaRepository<SubProfessionDetails, Integer> {

	List<SubProfessionDetails> findByIntegerTemporaryDetailId(Integer itId);

	@Transactional
    @Modifying
    @Query("DELETE FROM SubProfessionDetails spd WHERE spd.integerTemporaryDetailId = :temporaryId")
	void deleteByIntegerTemporaryDetailId(Integer temporaryId);
	
	@Query("SELECT spd FROM SubProfessionDetails spd WHERE spd.integerTemporaryDetailId = :itId ")
	List<SubProfessionDetails> findByIntegerTemporaryDetailIdAndProfessionName(Integer itId);

	@Transactional
	@Modifying
	@Query("DELETE FROM SubProfessionDetails i WHERE i.userId = :userId")
	void deleteByuserId(Integer userId);

}
