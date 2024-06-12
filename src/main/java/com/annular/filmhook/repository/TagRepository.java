package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag,Integer> {

}
