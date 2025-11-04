package com.annular.filmhook.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.User;
import com.annular.filmhook.model.UserWorkExperience;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserWorkExperienceRepository extends JpaRepository<UserWorkExperience, Integer> {
    List<UserWorkExperience> findByUser(User user);
    
    Optional<UserWorkExperience> findByUserAndCompanyNameIgnoreCaseAndDesignationIgnoreCase(
            User user, String companyName, String designation);

}
