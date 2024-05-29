package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Link;

@Repository
public interface LinkRepository extends JpaRepository<Link, Integer> {

}
