package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.LiveStreamComment;

@Repository
public interface LiveStreamCommentRepository extends JpaRepository<LiveStreamComment, Integer>  {

	@Query("SELECT l FROM LiveStreamComment l WHERE l.liveChannelId = :liveChannelId AND l.liveStreamCommenIsActive = true")
	List<LiveStreamComment> findByLiveChannelId(Integer liveChannelId);


}
