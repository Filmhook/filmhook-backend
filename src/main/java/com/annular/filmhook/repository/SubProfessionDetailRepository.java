package com.annular.filmhook.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.SubProfessionDetails;

@Repository
public interface SubProfessionDetailRepository extends JpaRepository<SubProfessionDetails, Integer> {

	List<SubProfessionDetails> findByIntegerTemporaryDetailId(Integer itId);

}
