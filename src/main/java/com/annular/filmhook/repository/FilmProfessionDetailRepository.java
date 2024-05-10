package com.annular.filmhook.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.FilmProfessionDetails;

@Repository
public interface FilmProfessionDetailRepository extends JpaRepository<FilmProfessionDetails, Integer> {

	List<FilmProfessionDetails> findByProfessionTemporaryDetailId(Integer itId);

	@Transactional
    @Modifying
    @Query("DELETE FROM FilmProfessionDetails pd WHERE pd.professionTemporaryDetailId = :temporaryId")
	void deleteByProfessionTemporaryDetailId(Integer temporaryId);

	@Transactional
	@Modifying
	@Query("DELETE FROM FilmProfessionDetails i WHERE i.userId = :userId")
	void deleteByUserId(Integer userId);

}
