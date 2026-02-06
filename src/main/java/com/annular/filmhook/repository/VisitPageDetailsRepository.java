package com.annular.filmhook.repository;

import com.annular.filmhook.model.VisitPageDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VisitPageDetailsRepository extends JpaRepository<VisitPageDetails, Integer> {
    List<VisitPageDetails> findByVisitPage_VisitPageId(Integer visitPageId);
}
