package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.ShootingLocationPayment;

@Repository
public interface ShootingLocationPaymentRepository extends JpaRepository<ShootingLocationPayment, Integer> {
    Optional<ShootingLocationPayment> findByTxnid(String txnid);
    boolean existsByTxnid(String txnid);
    
    Optional<ShootingLocationPayment> findByBooking_IdAndStatus(Integer bookingId, String status);
    


}
