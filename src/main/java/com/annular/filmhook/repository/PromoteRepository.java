package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Promote;

@Repository
public interface PromoteRepository extends JpaRepository<Promote, Integer> {

    @Query("select p from Promote p where p.userId=:userId and p.status=true")
    List<Promote> findByUserId(Integer userId);

    @Query("select um from Promote um where um.userId = :id and um.postId = :postId")
    Promote findByPostIdAndUserId(Integer postId, Integer id);

	Promote findByPostId(Integer postId);

	@Query("select p from Promote p where p.promoteId=:promoteId")
	Optional<Promote> findByPromoteId(Integer promoteId);

	@Query("SELECT COUNT(p) > 0 FROM Promote p WHERE p.postId = :postId AND p.status = :status")
	boolean existsByPostIdAndStatus(@Param("postId") Integer postId, @Param("status") boolean status);


	@Query("SELECT p FROM Promote p WHERE p.postId = :postId AND p.status = true")
	Optional<Promote> findByPostIds(@Param("postId") Integer postId);
	
	 List<Promote> findByUserIdAndStatus(Integer userId, boolean status);


}
