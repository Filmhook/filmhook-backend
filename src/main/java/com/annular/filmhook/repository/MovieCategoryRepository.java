package com.annular.filmhook.repository;

import com.annular.filmhook.model.MovieCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieCategoryRepository extends JpaRepository<MovieCategory, Integer> {
}