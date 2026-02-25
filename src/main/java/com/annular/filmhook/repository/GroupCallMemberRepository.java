package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annular.filmhook.model.GroupCallMember;

public interface GroupCallMemberRepository extends JpaRepository<GroupCallMember, Integer> {
	GroupCallMember findByGroupCallIdAndUserId(Integer groupCallId, Integer userId);

	List<GroupCallMember> findByGroupCallId(Integer groupCallId);
}
