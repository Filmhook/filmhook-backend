package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.annular.filmhook.model.FilmSubProfessionDetails;

@Repository
public interface FilmSubProfessionDetailRepository extends JpaRepository<FilmSubProfessionDetails, Integer> {

    List<FilmSubProfessionDetails> findByIndustryTemporaryDetailId(Integer itId);

    @Transactional
    @Modifying
    @Query("DELETE FROM FilmSubProfessionDetails spd WHERE spd.industryTemporaryDetailId = :temporaryId")
    void deleteByIndustryTemporaryDetailId(Integer temporaryId);

    @Query("SELECT spd FROM FilmSubProfessionDetails spd WHERE spd.industryTemporaryDetailId = :itId ")
    List<FilmSubProfessionDetails> findByIndustryTemporaryDetailIdAndProfessionName(Integer itId);

    @Transactional
    @Modifying
    @Query("DELETE FROM FilmSubProfessionDetails i WHERE i.userId = :userId")
    void deleteByUserId(Integer userId);


}
