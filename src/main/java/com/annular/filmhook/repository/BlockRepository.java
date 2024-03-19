package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Block;

@Repository
public interface BlockRepository extends JpaRepository<Block, Integer>{

	@Query("select b from Block b where b.userId=:id and b.status=true")
	List<Block> findByUserId(Integer id);

}