package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.annular.filmhook.model.AuditionSubDetails;

public interface AuditionSubDetailsRepository extends JpaRepository<AuditionSubDetails, Integer> {
    List<AuditionSubDetails> findByAuditionDetails_AuditionDetailsId(Integer auditionDetailsId);






}

