package com.annular.filmhook.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.annular.filmhook.model.AuditionCompanyDetails;
import com.annular.filmhook.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

public interface AuditionCompanyRepository extends JpaRepository<AuditionCompanyDetails, Integer> {
    Optional<AuditionCompanyDetails> findByAccessCode(String accessCode);
    List<AuditionCompanyDetails> findAllByUser(User user);
    List<AuditionCompanyDetails> findByStatusTrue();
    List<AuditionCompanyDetails> findByStatusFalseAndVerificationStatus(AuditionCompanyDetails.VerificationStatus verificationStatus);
    List<AuditionCompanyDetails> findByUserAndVerificationStatusIn(User user, List<AuditionCompanyDetails.VerificationStatus> statuses);
    List<AuditionCompanyDetails> findAllByUserAndDeletedFalse(User user);

    List<AuditionCompanyDetails> findByVerificationStatusAndStatusAndDeletedFalse(
            AuditionCompanyDetails.VerificationStatus verificationStatus, Boolean status);
    
    @Query("SELECT DISTINCT c " +
            "FROM AuditionCompanyDetails c " +
            "LEFT JOIN AuditionUserCompanyRole r ON r.company = c " +
            "WHERE c.user.id = :userId OR r.assignedUser.id = :userId")
     List<AuditionCompanyDetails> findCompaniesForUser(@Param("userId") Integer userId);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE auditions_companies SET deleted = true, status = false WHERE user_id = :userId", nativeQuery = true)
    void softDeleteByUserId(@Param("userId") Integer userId);






}
