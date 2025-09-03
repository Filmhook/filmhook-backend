package com.annular.filmhook.repository;

import com.annular.filmhook.model.MovieSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovieSubCategoryRepository extends JpaRepository<MovieSubCategory, Integer> {
    List<MovieSubCategory> findByCategoryId(Integer categoryId);
}
