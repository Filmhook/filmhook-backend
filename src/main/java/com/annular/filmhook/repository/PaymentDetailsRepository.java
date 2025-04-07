package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.PaymentDetails;
import com.annular.filmhook.model.User;

@Repository
public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails,Integer>{

	Optional<PaymentDetails> findByPaymentId(Integer paymentId);

    @Query("SELECT COUNT(p) FROM PaymentDetails p")
    Integer getTotalCount();

    @Query("SELECT COUNT(p) FROM PaymentDetails p WHERE p.promotionStatus = :status")
    Integer getCountByPromotionStatus(@Param("status") String status);

}
