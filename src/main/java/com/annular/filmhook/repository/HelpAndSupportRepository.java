package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.HelpAndSupport;

@Repository
public interface HelpAndSupportRepository extends JpaRepository<HelpAndSupport, Integer>{

}
