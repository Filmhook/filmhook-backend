package com.annular.filmhook.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.annular.filmhook.model.GroupCall;
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

	GroupCallMember findTopByUserIdOrderByIdDesc(Integer uid);
	
	GroupCallMember findTopByUserIdAndLeaveTimeIsNullOrderByIdDesc(Integer userId);
	
	   @Transactional
	    @Modifying
	    @Query("UPDATE GroupCallMember g SET g.deleted = true "
	         + "WHERE g.userId = :userId AND g.groupCallId IN :groupCallIds")
	    void softDeleteGroupCalls(@Param("groupCallIds") List<Integer> groupCallIds,
	                              @Param("userId") Integer userId);


	    @Transactional
	    @Modifying
	    @Query("UPDATE GroupCallMember g SET g.deleted = true "
	         + "WHERE g.userId = :userId")
	    void softDeleteAllGroupCalls(@Param("userId") Integer userId);


	    @Query("SELECT g FROM GroupCall g JOIN GroupCallMember m ON g.id = m.groupCallId "
	    	     + "WHERE m.userId = :userId AND m.deleted = false")
	    	List<GroupCall> findActiveGroupCalls(@Param("userId") Integer userId);
	    
	    List<GroupCallMember> findByUserIdAndGroupCallIdIn(Integer userId, Set<Integer> groupCallIds);

	    @Query("SELECT g.groupCallId, u.id, u.name " +
	    	       "FROM GroupCallMember g JOIN User u ON u.id = g.userId " +
	    	       "WHERE g.groupCallId IN :groupIds")
	    	List<Object[]> findGroupMembersForGroups(@Param("groupIds") Set<Integer> groupIds);
}
