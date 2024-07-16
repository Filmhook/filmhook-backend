package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Location;
import com.annular.filmhook.webmodel.UserWebModel;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {

    Optional<Location> findByUser(Integer user);

    @Query(value = "SELECT RESULT.user_id FROM " +
            "( SELECT l.id, l.user_id, l.latitude, l.longitude, " +
            "       SQRT ( POW(69.1 * (l.latitude - :latitude), 2) + POW(69.1 * (:longitude - l.longitude) * COS(l.latitude / 57.3), 2) ) AS distance " +
            "   FROM location l " +
            "   WHERE l.user_id <> :loggedInUser" +
            "   HAVING distance <= :miles " +
            "   ORDER BY distance ) AS RESULT", nativeQuery = true)
    List<Integer> getNearByUsers(Integer loggedInUser, Double miles, Double latitude, Double longitude);
    
    @Query("SELECT u.id FROM User u WHERE u.id <> :userId")
    List<Integer> getAllUsersExceptLoggedIn(int userId);

	
}
