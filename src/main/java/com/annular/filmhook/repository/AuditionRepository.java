package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Audition;

@Repository
public interface AuditionRepository extends JpaRepository<Audition, Integer>{

}
