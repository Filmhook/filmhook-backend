package com.annular.filmhook.repository;

import com.annular.filmhook.model.Bookings;

import com.annular.filmhook.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingsRepository extends JpaRepository<Bookings, Integer> {

    List<Bookings> findByBookedUser(User bookedUser);

}
