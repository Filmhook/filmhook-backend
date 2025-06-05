package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annular.filmhook.model.SubLocationTypeContent;

public interface SubLocationTypeContentRepository extends JpaRepository<SubLocationTypeContent, Long> {

	List<SubLocationTypeContent> findBySubLocationTypeId(Long subLocationTypeId);

}
