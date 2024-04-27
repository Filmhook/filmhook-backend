package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.LiveSubscribe;

@Repository
public interface LiveSubscribeRepository extends JpaRepository<LiveSubscribe, Integer> {

	@Query("select l from LiveSubscribe l where l.liveChannelId = :liveChannelId")
    List<LiveSubscribe> findByChannelId(Integer liveChannelId);

}
