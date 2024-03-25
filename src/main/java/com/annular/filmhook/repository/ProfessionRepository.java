package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Profession;

@Repository
public interface ProfessionRepository extends JpaRepository<Profession, Integer> {

}
