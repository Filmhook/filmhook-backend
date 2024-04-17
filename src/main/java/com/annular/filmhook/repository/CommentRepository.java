package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Integer> {

    
	@Query("select c from Comment c where c.postId=:postId and c.status=true")
	Optional<Comment> findByIds(Integer postId);

}
