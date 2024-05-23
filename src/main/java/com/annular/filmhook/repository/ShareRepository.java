package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Share;

@Repository
public interface ShareRepository extends JpaRepository<Share,Integer> {

	@Query("SELECT COUNT(l) FROM Share l WHERE l.postId = :id and l.status=true")
	int countByMediaFileId(Integer id);

	@Query("select COUNT(s) from Share s where s.postId = :id")
	Integer getShareCount(Integer id);

    @Query("SELECT COUNT(s) FROM Share s WHERE s.postId = :postId AND s.status = true")
	Integer countSharesByPostId(Integer postId);

}
