package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.annular.filmhook.model.IndustryDetails;

@Repository
public interface IndustryDetailRepository extends JpaRepository<IndustryDetails, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM IndustryDetails id WHERE id.industryTemporaryDetailId = :temporaryId")
    void deleteByIndustryTemporaryDetailId(Integer temporaryId);

    @Transactional
    @Modifying
    @Query("DELETE FROM IndustryDetails i WHERE i.userId = :userId")
    void deleteByUserId(Integer userId);

}
