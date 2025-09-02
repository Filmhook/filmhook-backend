package com.annular.filmhook.service;

import com.annular.filmhook.model.MovieCategory;
import com.annular.filmhook.model.MovieSubCategory;

import java.util.List;

public interface MovieService {
    List<MovieCategory> getAllCategories();
    List<MovieSubCategory> getSubCategories(Integer categoryId);
}
