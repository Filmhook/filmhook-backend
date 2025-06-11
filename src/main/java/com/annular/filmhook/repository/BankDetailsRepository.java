package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.BankDetails;

@Repository
public interface BankDetailsRepository extends JpaRepository<BankDetails, Long> {
}
