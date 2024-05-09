package com.annular.filmhook.repository;

import java.util.List;

import javax.transaction.Transactional;

import com.annular.filmhook.model.Profession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.ProfessionPermanentDetail;

@Repository
public interface ProfessionPermanentDetailRepository extends JpaRepository<ProfessionPermanentDetail, Integer>{

	@Modifying
	@Transactional
	@Query("select ppd.professionPermanentId from ProfessionPermanentDetail ppd where ppd.platformPermanentDetail.platformPermanentId=:ppdId")
	List<Integer> findByPlatformPermanentDetailId(Integer ppdId);

	@Query("select p from ProfessionPermanentDetail p where p.profession in (:professionIds)")
	List<ProfessionPermanentDetail> getDataByProfessionIds(List<Profession> professionIds);

	List<ProfessionPermanentDetail> findByUserId(Integer userId);
}
