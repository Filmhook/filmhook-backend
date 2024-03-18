package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.UserProfilePin;

@Repository
public interface PinProfileRepository extends JpaRepository<UserProfilePin,Integer> {

	@Query("select up from UserProfilePin up where up.userId=:id and up.status=true")
	List<UserProfilePin> findByUserId(Integer id);

}
