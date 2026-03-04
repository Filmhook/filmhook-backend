package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.annular.filmhook.model.GroupCallMember;

public interface GroupCallMemberRepository extends JpaRepository<GroupCallMember, Integer> {
	//GroupCallMember findByGroupCallIdAndUserId(Integer groupCallId, Integer userId);

	List<GroupCallMember> findByGroupCallId(Integer groupCallId);

	@Query("SELECT u.id, u.name FROM GroupCallMember g JOIN User u ON u.id = g.userId " +
			"WHERE g.groupCallId = :groupCallId AND g.userId <> :userId")
	List<Object[]> findGroupMembers(@Param("groupCallId") Integer groupCallId,
			@Param("userId") Integer userId);
	
	@Query(value =
	        "SELECT * FROM group_call_members " +
	        "WHERE group_call_id = :groupCallId AND user_id = :userId " +
	        "LIMIT 1",
	        nativeQuery = true)
	GroupCallMember findByGroupCallIdAndUserId(@Param("groupCallId") Integer groupCallId,
	                                           @Param("userId") Integer userId);


}
