package com.annular.filmhook.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.annular.filmhook.model.AuditionCompanyDetails;
import com.annular.filmhook.model.User;

import java.util.List;
import java.util.Optional;

public interface AuditionCompanyRepository extends JpaRepository<AuditionCompanyDetails, Integer> {
    Optional<AuditionCompanyDetails> findByAccessCode(String accessCode);
    List<AuditionCompanyDetails> findAllByUser(User user);
    List<AuditionCompanyDetails> findByStatusTrue();
    List<AuditionCompanyDetails> findByStatusFalseAndVerificationStatus(AuditionCompanyDetails.VerificationStatus status);


}
