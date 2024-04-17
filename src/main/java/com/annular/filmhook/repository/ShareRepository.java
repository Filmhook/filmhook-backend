package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Share;

@Repository
public interface ShareRepository extends JpaRepository<Share,Integer> {

}
