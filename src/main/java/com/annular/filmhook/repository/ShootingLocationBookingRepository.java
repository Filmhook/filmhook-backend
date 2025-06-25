package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annular.filmhook.model.BookingStatus;
import com.annular.filmhook.model.ShootingLocationBooking;

public interface ShootingLocationBookingRepository extends JpaRepository<ShootingLocationBooking, Integer> {

    List<ShootingLocationBooking> findByProperty_Id(Integer propertyId);

    List<ShootingLocationBooking> findByClient_UserId(Integer clientId);

    Optional<ShootingLocationBooking> findByProperty_IdAndStatus(Integer propertyId, BookingStatus status);

    List<ShootingLocationBooking> findByProperty_IdAndClient_UserId(Integer propertyId, Integer userId);

}