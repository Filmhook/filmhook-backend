package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.MultiMediaFiles;

@Repository
public interface MultiMediaFileRepository extends JpaRepository<MultiMediaFiles, Integer> {

}
