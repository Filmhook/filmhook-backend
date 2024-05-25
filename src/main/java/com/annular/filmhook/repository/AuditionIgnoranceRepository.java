package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Audition;
import com.annular.filmhook.model.AuditionIgnoranceDetails;

@Repository
public interface AuditionIgnoranceRepository extends JpaRepository<AuditionIgnoranceDetails, Integer> {


    @Query("SELECT a FROM AuditionIgnoranceDetails a WHERE a.auditionRefId = :auditionRefId AND a.auditionIgnoranceUser = :auditionIgnoranceUser")
    Optional<AuditionIgnoranceDetails> findByAuditionRefIdAndAuditionIgnoranceUser(Integer auditionRefId, Integer auditionIgnoranceUser);

//    @Query("select a from AuditionIgnoranceDetails a where a.auditionIgnoranceUser =:userId")
//	List<Integer> findIgnoredAuditionIdsByUserId(Integer userId);

    @Query("SELECT ai.auditionRefId FROM AuditionIgnoranceDetails ai WHERE ai.auditionIgnoranceUser = :userId")
    List<Integer> findIgnoredAuditionIdsByUserId(Integer userId);

}
