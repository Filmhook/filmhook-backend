package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.UserMediaPin;

@Repository
public interface PinMediaRepository extends JpaRepository<UserMediaPin,Integer> {

	@Query("select um from UserMediaPin um where um.userId=:id and um.status=true")
	List<UserMediaPin> findByUserId(Integer id);

}
