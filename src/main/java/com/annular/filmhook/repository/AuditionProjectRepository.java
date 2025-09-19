package com.annular.filmhook.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.AuditionNewProject;

@Repository
public interface AuditionProjectRepository extends JpaRepository<AuditionNewProject, Integer> {
	
	List<AuditionNewProject> findAllByCompanyId(Integer companyId);

}