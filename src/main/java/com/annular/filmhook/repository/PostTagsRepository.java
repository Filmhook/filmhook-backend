package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.annular.filmhook.model.PostTags;

@Repository
public interface PostTagsRepository extends JpaRepository<PostTags, Integer> {

	
	List<PostTags> findByTaggedUserUserIdAndStatusTrue(Integer userId);
	  @Transactional
	    void deleteByPostId(Integer postId);
	  
	  @Modifying
	  @Query("DELETE FROM PostTags p WHERE p.postId = :postId AND p.taggedUser.userId IN :taggedUserIds")
	  void deleteByPostIdAndTaggedUserIds(@Param("postId") Integer postId, @Param("taggedUserIds") List<Integer> taggedUserIds);


}
