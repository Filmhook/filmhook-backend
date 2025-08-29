package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.VisitPage;

@Repository
public interface VisitPageRepository extends JpaRepository<VisitPage, Integer> {
	
	 // Fetch by category id
    List<VisitPage> findByCategory_CategoryId(Integer categoryId);

}
