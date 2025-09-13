package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annular.filmhook.model.AuditionCompanyDetails;
import com.annular.filmhook.model.AuditionUserCompanyRole;
import com.annular.filmhook.model.User;

public interface AuditionUserCompanyRoleRepository extends JpaRepository<AuditionUserCompanyRole, Integer> {
	
	boolean existsByCompanyAndAssignedUser(AuditionCompanyDetails company, User assignedUser);
	
	Optional<AuditionUserCompanyRole> findByCompanyIdAndAssignedUser_UserId(Integer companyId, Integer userId);
	
	 Optional<AuditionUserCompanyRole> findByFilmHookCode(String filmHookCode);
	 
//	  Optional<AuditionUserCompanyRole> findByOwner_UserId(Integer ownerId);
	    List<AuditionUserCompanyRole> findAllByOwner_UserId(Integer ownerId);


}