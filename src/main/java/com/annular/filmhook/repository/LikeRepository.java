package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Likes;

@Repository
public interface LikeRepository extends JpaRepository<Likes, Integer> {

	@Query("select l from Likes l where l.postId = :id and l.likedBy = :userId and l.category = 'Post'")
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
    
    @Query("SELECT COUNT(l) FROM Likes l WHERE l.category = :category AND l.postId = :postId AND l.commentId = :commentId AND l.auditionId = :auditionId AND l.status = :status")
    Integer countByCategoryAndTargetAndStatus(
            @Param("category") String category,
            @Param("postId") Integer postId,
            @Param("commentId") Integer commentId,
            @Param("auditionId") Integer auditionId,
            @Param("status") Boolean status
    );
    
    @Query("SELECT COUNT(l) FROM Likes l " +
    	       "WHERE l.category = :category " +
    	       "AND (:postId IS NULL OR l.postId = :postId) " +
    	       "AND (:commentId IS NULL OR l.commentId = :commentId) " +
    	       "AND (:auditionId IS NULL OR l.auditionId = :auditionId) " +
    	       "AND l.reactionType = :reactionType")
    	Integer countByReactionType(
    	        @Param("category") String category,
    	        @Param("postId") Integer postId,
    	        @Param("commentId") Integer commentId,
    	        @Param("auditionId") Integer auditionId,
    	        @Param("reactionType") String reactionType
    	);
    Long countByPostIdAndReactionType(Integer postId, String reactionType);

}
