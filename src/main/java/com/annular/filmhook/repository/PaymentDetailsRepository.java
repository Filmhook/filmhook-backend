package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.PaymentDetails;
import com.annular.filmhook.model.User;

@Repository
public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails,Integer>{

	Optional<PaymentDetails> findByPaymentId(Integer paymentId);

}
