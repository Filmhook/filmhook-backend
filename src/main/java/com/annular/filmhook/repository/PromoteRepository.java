package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Promote;

@Repository
public interface PromoteRepository extends JpaRepository<Promote, Integer> {

    @Query("select p from Promote p where p.userId=:userId and p.status=true")
    List<Promote> findByUserId(Integer userId);

    @Query("select um from Promote um where um.userId = :id and um.postId = :postId")
    Promote findByPostIdAndUserId(Integer postId, Integer id);

	Promote findByPostId(Integer postId);

}
