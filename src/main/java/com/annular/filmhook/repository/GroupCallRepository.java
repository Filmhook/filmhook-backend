package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.annular.filmhook.model.GroupCall;

public interface GroupCallRepository extends JpaRepository<GroupCall, Integer> {
	
	  @Query(value =
	            "SELECT g.* FROM group_call g " +
	            "JOIN group_call_members m ON g.id = m.group_call_id " +
	            "WHERE m.user_id = :userId " +
	            "ORDER BY g.created_on DESC",
	            nativeQuery = true)
	    List<GroupCall> findGroupCalls(@Param("userId") Integer userId);

}
