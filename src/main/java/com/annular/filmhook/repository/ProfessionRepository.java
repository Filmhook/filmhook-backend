package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import com.annular.filmhook.model.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Profession;

@Repository
public interface ProfessionRepository extends JpaRepository<Profession, Integer> {

    List<Profession> findByPlatform(Platform platform);

    Optional<Profession> findByProfessionName(String professionName);
}
