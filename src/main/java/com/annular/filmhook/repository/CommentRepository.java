package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("Select c from Comment c Where c.parentCommentId = :commentId and c.postId = :postId and c.status = true")
    List<Comment> getChildComments(Integer postId, Integer commentId);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId AND c.status = true")
    int countActiveCommentsByPostId(@Param("postId") Integer postId);
    
    Optional<Comment> findByCommentId(Integer commentId);


}
