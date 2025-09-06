package com.annular.filmhook.controller;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.MovieCategory;
import com.annular.filmhook.model.MovieSubCategory;
import com.annular.filmhook.service.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        try {
            logger.info("Fetching movie categories");
            List<MovieCategory> categories = movieService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.error("Error in getCategories: {}", e.getMessage(), e);
            return ResponseEntity.ok(new Response(-1, "Failed to fetch categories", null));
        }
    }

    @GetMapping("/categories/{id}/subcategories")
    public ResponseEntity<?> getSubCategories(@PathVariable Integer id) {
        try {
            logger.info("Fetching subcategories for category id: {}", id);
            List<MovieSubCategory> subCategories = movieService.getSubCategories(id);
            return ResponseEntity.ok(subCategories);
        } catch (Exception e) {
            logger.error("Error in getSubCategories: {}", e.getMessage(), e);
            return ResponseEntity.ok(new Response(-1, "Failed to fetch subcategories", null));
        }
    }
}
