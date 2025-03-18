package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.PaymentDetails;

@Repository
public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails,Integer>{

}
