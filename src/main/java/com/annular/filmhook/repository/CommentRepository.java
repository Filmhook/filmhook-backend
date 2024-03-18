package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Integer> {

}
