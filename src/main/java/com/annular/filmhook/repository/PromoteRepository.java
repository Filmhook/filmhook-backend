package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Promote;

@Repository
public interface PromoteRepository extends JpaRepository<Promote,Integer>{

	@Query("select p from Promote p where p.userId=:userId and p.status=true")
	List<Promote> findByUserId(Integer userId);

}
