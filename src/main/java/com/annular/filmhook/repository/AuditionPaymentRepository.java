package com.annular.filmhook.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.AuditionNewProject;
import com.annular.filmhook.model.AuditionPayment;

@Repository
public interface AuditionPaymentRepository extends JpaRepository<AuditionPayment, Integer> {
	  Optional<AuditionPayment> findByTxnid(String txnid);
	  
	  boolean existsByTxnid(String txnid);
	  
	   List<AuditionPayment> findByPaymentStatusAndExpiryDateTimeBefore(String status, LocalDateTime time);

	    boolean existsByProjectAndPaymentStatusAndExpiryDateTimeAfter(
	        AuditionNewProject project,
	        String status,
	        LocalDateTime time
	    );
	    
	    Optional<AuditionPayment> findTopByProjectIdOrderByExpiryDateTimeDesc(Integer projectId);
}


