package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.SubProfesssion;

@Repository
public interface SubProfesssionRepository extends JpaRepository<SubProfesssion, Integer> {

}
