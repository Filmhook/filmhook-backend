package com.annular.filmhook.repository;

import com.annular.filmhook.model.Posts;

import com.annular.filmhook.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Integer> {

    @Query("SELECT p FROM Posts p WHERE p.user=:userId AND p.status=true ORDER BY p.createdOn DESC")
    List<Posts> getUserPosts(User userId);

    Optional<Posts> findByPromoteFlag(Boolean promoteFlag);

    Posts findByPostId(String postId);

    @Query("SELECT p FROM Posts p WHERE p.user = :userId AND p.promoteStatus = true")
    List<Posts> findByUsers(User userId);

    List<Posts> findAllByPromoteFlag(boolean b);

    @Query("SELECT p FROM Posts p WHERE p.status = true ORDER BY p.createdOn DESC")
    List<Posts> getAllActivePosts(Pageable paging);

    @Query("SElECT p FROM Posts p where p.id = :list and p.user.userId = :userId")
	Optional<Posts> findByIdAndUserId(List<Integer> list, Integer userId);

    // List<Posts> findByPromoteFlagAndUserId(boolean b, Integer id);
    @Query("SELECT p FROM Posts p where p.id = :postId")
   	Optional<Posts> findByPostId(Integer postId);

    @Query("SELECT p FROM Posts p WHERE p.tagUsers LIKE %:userId%")
    List<Posts> getPostsByTaggedUserId(String userId);

    @Query("SELECT p FROM Posts p where p.id = :postId")
	Optional<Posts> findByIds(Integer postId);

//    @Modifying
//    @Query("UPDATE Posts p SET p.promoteStatus = :b WHERE p.id = :postId")
//	void updatePromoteStatus(Integer postId, boolean b);

    @Query("SELECT p FROM Posts p WHERE p.status = true")
	List<Posts> getAllActivePosts();

    @Modifying
    @Query("UPDATE Posts p SET p.promoteStatus = :promoteStatus, p.promoteFlag = :promoteFlag WHERE p.id = :postId")
    void updatePromoteStatusAndFlag(Integer postId,boolean promoteStatus, boolean promoteFlag);
    
    
    @Query("SELECT COUNT(p) FROM Posts p WHERE p.createdOn BETWEEN :startDate AND :endDate")
    int getTotalPostCount(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    @Modifying
    @Query("UPDATE Posts p SET p.viewsCount = p.viewsCount + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Integer postId);




}
