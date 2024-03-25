package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Industry;

@Repository
public interface IndustryRepository extends JpaRepository<Industry, Integer> {

}