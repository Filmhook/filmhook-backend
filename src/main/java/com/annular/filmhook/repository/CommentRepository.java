package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("Select c from Comment c Where c.parentCommentId = :commentId and c.postId = :postId and c.status = true")
    List<Comment> getChildComments(Integer postId, Integer commentId);

}
