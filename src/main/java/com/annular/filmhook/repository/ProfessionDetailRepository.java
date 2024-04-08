package com.annular.filmhook.repository;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.ProfesssionDetails;

@Repository
public interface ProfessionDetailRepository extends JpaRepository<ProfesssionDetails, Integer> {

	List<ProfesssionDetails> findByProfessionTemporaryDetailId(Integer itId);

	@Transactional
    @Modifying
    @Query("DELETE FROM ProfesssionDetails pd WHERE pd.professionTemporaryDetailId = :temporaryId")
	void deleteByProfessionTemporaryDetailId(Integer temporaryId);

}
