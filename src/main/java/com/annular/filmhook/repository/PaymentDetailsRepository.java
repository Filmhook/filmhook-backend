package com.annular.filmhook.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.PaymentDetails;
import com.annular.filmhook.model.User;

@Repository
public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails,Integer>{

	Optional<PaymentDetails> findByPaymentId(Integer paymentId);

	@Query("SELECT COUNT(p) FROM PaymentDetails p WHERE p.promotionStatus IN ('SUCCESS', 'PENDING', 'FAILED', 'EXPIRED') AND p.createdOn BETWEEN :startDate AND :endDate")
	Integer getTotalCount(@Param("startDate") Date startDate, @Param("endDate") Date endDate);


	@Query("SELECT COUNT(p) FROM PaymentDetails p WHERE p.promotionStatus = :status AND p.createdOn BETWEEN :startDate AND :endDate")
	Integer getCountByPromotionStatus(@Param("status") String status,
	                                  @Param("startDate") Date startDate,
	                                  @Param("endDate") Date endDate);


    @Query("SELECT p FROM PaymentDetails p WHERE p.promotionStatus = :status")
    List<PaymentDetails> findByPromotionStatus(@Param("status") String status);

    @Query("SELECT p FROM PaymentDetails p WHERE p.createdOn BETWEEN :startDate AND :endDate")
    Page<PaymentDetails> findByCreatedOnBetween(@Param("startDate") Date startDate,
                                                @Param("endDate") Date endDate,
                                                Pageable pageable);

    @Query("SELECT p FROM PaymentDetails p WHERE p.promotionStatus = :status AND p.createdOn BETWEEN :startDate AND :endDate")
    Page<PaymentDetails> findByPromotionStatusAndCreatedOnBetween(@Param("status") String status,
                                                                  @Param("startDate") Date startDate,
                                                                  @Param("endDate") Date endDate,
                                                                  Pageable pageable);

    @Query("SELECT COUNT(p) FROM PaymentDetails p WHERE (p.notificationCount IS NULL OR p.notificationCount = false) AND p.status = true")
    Integer countByNotificationCountIsNullOrNotificationCountFalseAndStatusTrue();





}
