package com.annular.filmhook.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.User;
import com.annular.filmhook.model.UserWorkExperience;

import java.util.List;

@Repository
public interface UserWorkExperienceRepository extends JpaRepository<UserWorkExperience, Integer> {
    List<UserWorkExperience> findByUser(User user);
}
