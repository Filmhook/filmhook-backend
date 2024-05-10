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
}
