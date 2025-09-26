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

	List<AuditionUserCompanyRole> findAllByOwner_UserId(Integer ownerId);

    Optional<AuditionUserCompanyRole> findByCompanyAndAssignedUser(AuditionCompanyDetails company, User assignedUser);

    // ✅ Check if role exists AND has a specific status (true=active, false=soft-deleted)
    Optional<AuditionUserCompanyRole> findByCompanyAndAssignedUserAndStatus(
            AuditionCompanyDetails company,
            User assignedUser,
            Boolean status
    );
    
    Optional<AuditionUserCompanyRole> findByFilmHookCodeAndDesignationAndAccessKeyIgnoreCaseAndStatusTrue(
            String filmHookCode,
            String designation,
            String accessKey
    );
    
    List<AuditionUserCompanyRole> findByOwner_UserIdAndCompany_IdAndDeletedFalse(Integer ownerId, Integer companyId);


}