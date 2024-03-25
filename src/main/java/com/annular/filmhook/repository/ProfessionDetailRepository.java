package com.annular.filmhook.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.ProfesssionDetails;

@Repository
public interface ProfessionDetailRepository extends JpaRepository<ProfesssionDetails, Integer> {

	List<ProfesssionDetails> findByProfessionTemporaryDetailId(Integer itId);

}
