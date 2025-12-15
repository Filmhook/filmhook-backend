package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annular.filmhook.model.IndustrySignupDetails;

public interface IndustrySignupDetailsRepository extends JpaRepository<IndustrySignupDetails, Integer>{
	  Optional<IndustrySignupDetails> findByUser_UserId(Integer userId);
}
