package com.annular.filmhook.repository;

import com.annular.filmhook.model.FilmSubProfession;
import com.annular.filmhook.model.FilmSubProfessionPermanentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilmSubProfessionPermanentDetailsRepository extends JpaRepository<FilmSubProfessionPermanentDetail, Integer> {

    @Query("select p from FilmSubProfessionPermanentDetail p where p.filmSubProfession in (:subProfessionIds)")
    List<FilmSubProfessionPermanentDetail> getDataBySubProfessionIds(List<FilmSubProfession> subProfessionIds);

    @Query("select p from FilmSubProfessionPermanentDetail p where p.userId=:userId and p.status=true")
    List<FilmSubProfessionPermanentDetail> getProfessionDataByUserId(Integer userId);

    @Query("select distinct(p.userId) from FilmSubProfessionPermanentDetail p where p.filmSubProfession in (:subProfessionIds)")
    List<Integer> getUsersBySubProfessionIds(List<FilmSubProfession> subProfessionIds);

	List<FilmSubProfessionPermanentDetail> findByUserId(Integer userId);
}
