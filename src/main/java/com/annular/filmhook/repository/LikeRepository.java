package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Likes;

@Repository
public interface LikeRepository extends JpaRepository<Likes,Integer>{
	
    @Query("select l from Likes l where l.userId=:userId and l.postId=:postId")
	Optional<Likes> findByUserIdAndPostId(Integer userId, Integer postId);

    @Query("SELECT COUNT(l) FROM Likes l WHERE l.postId = :id and l.status=true")
	int countByMediaFileId(Integer id);

    @Query("select COUNT(l) from Likes l where l.postId = :id ")
	Integer getLikeCount(Integer id);
	
	

}
