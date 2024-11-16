package com.annular.filmhook.repository;

import com.annular.filmhook.model.FilmSubProfession;
import com.annular.filmhook.model.FilmSubProfessionPermanentDetail;
import com.annular.filmhook.webmodel.ExperienceDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilmSubProfessionPermanentDetailsRepository extends JpaRepository<FilmSubProfessionPermanentDetail, Integer> {

    @Query("select p from FilmSubProfessionPermanentDetail p where p.filmSubProfession in (:subProfessionIds)")
    List<FilmSubProfessionPermanentDetail> getDataBySubProfessionIds(List<FilmSubProfession> subProfessionIds);

    @Query("select p from FilmSubProfessionPermanentDetail p where p.userId=:userId and p.status=true")
    List<FilmSubProfessionPermanentDetail> getProfessionDataByUserId(Integer userId);

    @Query("select distinct(p.userId) from FilmSubProfessionPermanentDetail p where p.filmSubProfession in (:subProfessionIds)")
    List<Integer> getUsersBySubProfessionIds(List<FilmSubProfession> subProfessionIds);

    List<FilmSubProfessionPermanentDetail> findByUserId(Integer userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM FilmSubProfessionPermanentDetail f WHERE f.platformPermanentDetail.platformPermanentId = :platformPermanentId")
	void deleteByPlatformPermanentDetailId(Integer platformPermanentId);

    @Query("SELECT new com.annular.filmhook.webmodel.ExperienceDTO(u.userId, MIN(u.startingYear), MAX(u.endingYear), " +
    	       "(MAX(u.endingYear) - MIN(u.startingYear))) " +
    	       "FROM FilmSubProfessionPermanentDetail u " +
    	       "WHERE u.userId = :userId " +
    	       "GROUP BY u.userId")
    	Optional<ExperienceDTO> calculateExperienceForUser(@Param("userId") Integer userId);


}
