package com.annular.filmhook.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annular.filmhook.model.PaymentModule;
import com.annular.filmhook.model.Payments;




public interface PaymentsRepository extends JpaRepository<Payments,Integer>{
	  Optional<Payments> findByTxnid(String txnid);

	    boolean existsByTxnid(String txnid);

	    List<Payments> findByPaymentStatusAndExpiryDateBefore(String status, LocalDateTime date);

	    boolean existsByReferenceIdAndModuleTypeAndPaymentStatus(
	            Integer referenceId,
	            PaymentModule moduleType,
	            String status
	    );
}
