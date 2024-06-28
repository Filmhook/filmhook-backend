package com.annular.filmhook.repository;

import java.util.List;

import javax.transaction.Transactional;

import com.annular.filmhook.model.FilmProfession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.FilmProfessionPermanentDetail;

@Repository
public interface FilmProfessionPermanentDetailRepository extends JpaRepository<FilmProfessionPermanentDetail, Integer> {

    @Modifying
    @Transactional
    @Query("select ppd.professionPermanentId from FilmProfessionPermanentDetail ppd where ppd.platformPermanentDetail.platformPermanentId=:ppdId")
    List<Integer> findByPlatformPermanentDetailId(Integer ppdId);

    @Query("select p from FilmProfessionPermanentDetail p where p.filmProfession in (:professionIds)")
    List<FilmProfessionPermanentDetail> getDataByProfessionIds(List<FilmProfession> professionIds);

    @Query("select distinct(p.userId) from FilmProfessionPermanentDetail p where p.filmProfession in (:professionIds)")
    List<Integer> getUsersByProfessionIds(List<FilmProfession> professionIds);

    @Query("select p from FilmProfessionPermanentDetail p where p.userId=:userId and p.status=true")
    List<FilmProfessionPermanentDetail> getProfessionDataByUserId(Integer userId);

    List<FilmProfessionPermanentDetail> findByUserId(Integer userId);
}
