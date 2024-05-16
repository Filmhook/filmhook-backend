package com.annular.filmhook.repository;

import com.annular.filmhook.model.Bookings;

import com.annular.filmhook.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingsRepository extends JpaRepository<Bookings, Integer> {

    List<Bookings> findByBookedUser(User bookedUser);

    @Query("Select br From Bookings br Where (br.fromDate like :fromDate or br.fromDate like :toDate) and (br.toDate like :fromDate or br.toDate like :toDate) and br.bookingStatus='Pending' and br.bookedUser=:bookedUser")
    List<Bookings> getPendingBookingsByUserFromAndToDates(User bookedUser, String fromDate, String toDate);

}
