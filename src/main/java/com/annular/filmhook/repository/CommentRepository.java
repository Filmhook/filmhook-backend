package com.annular.filmhook.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Integer> {

	@Query("select c from Comment c where c.postId=:postId and c.status=true")
	List<Comment> findByIds(Integer postId);

	@Query("SELECT COUNT(l) FROM Comment l WHERE l.postId = :id and l.status=true")
	int countByMediaFileId(Integer id);

	@Query("select COUNT(c) from Comment c where c.postId=:id and c.status=true")
	Integer getCommentCount(Integer id);

    

}
