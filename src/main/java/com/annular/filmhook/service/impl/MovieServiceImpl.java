package com.annular.filmhook.service.impl;

import com.annular.filmhook.model.MovieCategory;
import com.annular.filmhook.model.MovieSubCategory;
import com.annular.filmhook.repository.MovieCategoryRepository;
import com.annular.filmhook.repository.MovieSubCategoryRepository;
import com.annular.filmhook.service.MovieService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieCategoryRepository categoryRepo;
    private final MovieSubCategoryRepository subCategoryRepo;

    public MovieServiceImpl(MovieCategoryRepository categoryRepo,
                            MovieSubCategoryRepository subCategoryRepo) {
        this.categoryRepo = categoryRepo;
        this.subCategoryRepo = subCategoryRepo;
    }

    @Override
    public List<MovieCategory> getAllCategories() {
        return categoryRepo.findAll();
    }

    @Override
    public List<MovieSubCategory> getSubCategories(Integer categoryId) {
        return subCategoryRepo.findByCategoryId(categoryId);
    }
}
