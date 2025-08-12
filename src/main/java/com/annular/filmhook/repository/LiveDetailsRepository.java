package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.LiveChannel;

@Repository
public interface LiveDetailsRepository extends JpaRepository<LiveChannel, Integer> {
	
	Optional<LiveChannel> findByLiveId(String liveId);


}
