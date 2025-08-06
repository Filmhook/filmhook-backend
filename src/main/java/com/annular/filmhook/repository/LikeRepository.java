package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Likes;

@Repository
public interface LikeRepository extends JpaRepository<Likes, Integer> {

    @Query("select l from Likes l where l.postId = :id and l.likedBy = :userId and l.category = 'Post' and l.status = true")
    Optional<Likes> findByPostIdAndUserId(Integer id, Integer userId);

    @Query("select count(l) from Likes l where l.commentId = :commentId and l.postId = :postId and l.category = 'Comment' and l.status = true")
    Integer getLikesForCommentByCommentId(Integer postId, Integer commentId);
    
    List<Likes> findByStatusTrueAndNotifiedFalse();


    long countByCategoryAndAuditionIdAndStatusTrue(String category, Integer auditionId);

    boolean existsByCategoryAndAuditionIdAndLikedByAndStatusTrue(String category, Integer auditionId, Integer likedBy);

    
    Optional<Likes> findByCategoryAndLikedByAndPostIdAndCommentIdAndAuditionId(
    	    String category,
    	    Integer likedBy,
    	    Integer postId,
    	    Integer commentId,
    	    Integer auditionId
    	);
    
    Optional<Likes> findByCategoryAndAuditionIdAndLikedByAndStatusTrue(String category, Integer auditionId, Integer likedBy);
    List<Likes> findTop2ByPostIdAndStatusTrueOrderByCreatedOnDesc(Integer postId);

}
