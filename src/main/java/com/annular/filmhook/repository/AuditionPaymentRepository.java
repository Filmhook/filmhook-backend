package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.AuditionPayment;

@Repository
public interface AuditionPaymentRepository extends JpaRepository<AuditionPayment, Integer> {
	  Optional<AuditionPayment> findByTxnid(String txnid);
	  
	  boolean existsByTxnid(String txnid);
}


