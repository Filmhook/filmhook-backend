package com.annular.filmhook.repository;

import com.annular.filmhook.model.FilmProfession;
import com.annular.filmhook.model.Platform;
import com.annular.filmhook.model.PlatformFilmProfessionMap;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlatformFilmProfessionMapRepository extends JpaRepository<PlatformFilmProfessionMap, Integer> {

    @Query("Select pf.filmProfession From PlatformFilmProfessionMap pf Where pf.platform=:platform and pf.status=true")
    List<FilmProfession> getFilmProfessionsByPlatform(Platform platform);

}
