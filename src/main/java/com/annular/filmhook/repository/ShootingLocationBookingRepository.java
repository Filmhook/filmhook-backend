package com.annular.filmhook.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.annular.filmhook.model.BookingStatus;
import com.annular.filmhook.model.ShootingLocationBooking;

public interface ShootingLocationBookingRepository extends JpaRepository<ShootingLocationBooking, Integer> {

    List<ShootingLocationBooking> findByProperty_Id(Integer propertyId);

    List<ShootingLocationBooking> findByClient_UserId(Integer clientId);

    List<ShootingLocationBooking> findByProperty_IdAndStatus(Integer propertyId, BookingStatus status);

    List<ShootingLocationBooking> findByProperty_IdAndClient_UserId(Integer propertyId, Integer userId);
    
    @Query("SELECT b FROM ShootingLocationBooking b WHERE b.property.id = :propertyId AND b.status = 'CONFIRMED' AND " +
    	       "(b.shootStartDate <= :shootEndDate AND b.shootEndDate >= :shootStartDate)")
    	List<ShootingLocationBooking> findOverlappingBookings(@Param("propertyId") Integer propertyId,
    	                                                       @Param("shootStartDate") LocalDate shootStartDate,
    	                                                       @Param("shootEndDate") LocalDate shootEndDate);
    
    List<ShootingLocationBooking> findByShootEndDate(LocalDate endDate);
 

    List<ShootingLocationBooking> findByShootEndDateLessThanEqualAndStatus(LocalDate date, BookingStatus status);
    
    @Query("SELECT b FROM ShootingLocationBooking b " +
            "WHERE ((b.client.userId = :user1 AND b.property.user.id = :user2) " +
            "   OR (b.client.userId = :user2 AND b.property.user.id = :user1))")
     List<ShootingLocationBooking> findBookingsBetweenUsers(@Param("user1") Integer user1,
                                                             @Param("user2") Integer user2);
    
//   chat between perticular client and owner for booked property
    @Query("SELECT b FROM ShootingLocationBooking b " +
            "WHERE ((b.client.userId = :user1 AND b.property.user.userId = :user2) " +
            "    OR (b.client.userId = :user2 AND b.property.user.userId = :user1)) " +
            "AND b.property.id = :propertyId")
     List<ShootingLocationBooking> findBookingsBetweenUsersAndProperty(@Param("user1") Integer user1,
                                                                        @Param("user2") Integer user2,
                                                                        @Param("propertyId") Integer propertyId);
    



    Optional<ShootingLocationBooking> findById(Integer bookingId);

    
    @Query("SELECT COUNT(b) FROM ShootingLocationBooking b " +
            "WHERE b.property.id = :propertyId " +
            "AND b.createdAt >= :since " +
            "AND b.status = com.annular.filmhook.model.BookingStatus.CONFIRMED")
     long countConfirmedBookingsByPropertySince(
             @Param("propertyId") Integer propertyId,
             @Param("since") LocalDateTime since
     );


}