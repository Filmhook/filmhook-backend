package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.IndustryUserPermanentDetails;

@Repository
public interface IndustryUserPermanentDetailsRepository extends JpaRepository<IndustryUserPermanentDetails, Integer> {

	@Query("SELECT iupd FROM IndustryUserPermanentDetails iupd WHERE iupd.userId = :userId")
	List<IndustryUserPermanentDetails> findByUserId(Integer userId);

	@Query("SELECT iupd FROM IndustryUserPermanentDetails iupd WHERE iupd.userId = :userId and iupd.industriesName = :industriesName")
	Optional<IndustryUserPermanentDetails> findByUserIdAndIndustriesName(Integer userId, String industriesName);

}
