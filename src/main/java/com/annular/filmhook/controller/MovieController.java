package com.annular.filmhook.controller;

import com.annular.filmhook.model.MovieCategory;
import com.annular.filmhook.model.MovieSubCategory;
import com.annular.filmhook.service.MovieService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/categories")
    public List<MovieCategory> getCategories() {
        return movieService.getAllCategories();
    }

    @GetMapping("/categories/{id}/subcategories")
    public List<MovieSubCategory> getSubCategories(@PathVariable Integer id) {
        return movieService.getSubCategories(id);
    }
}

