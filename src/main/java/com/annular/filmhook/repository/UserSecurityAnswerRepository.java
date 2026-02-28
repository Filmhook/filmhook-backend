package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annular.filmhook.model.UserSecurityAnswer;

public interface UserSecurityAnswerRepository extends JpaRepository<UserSecurityAnswer, Integer> {
	 // ✅ Get all active answers of a user
    List<UserSecurityAnswer> findByUser_UserIdAndStatusTrue(Integer userId);

    // ✅ Delete all answers of a user (used before saving new 5)
    void deleteByUser_UserId(Integer userId);

    // ✅ Check if user already configured security questions
    boolean existsByUser_UserIdAndStatusTrue(Integer userId);

    // ✅ Get answers by user and specific question
    List<UserSecurityAnswer> findByUser_UserIdAndQuestion_IdAndStatusTrue(
            Integer userId,
            Integer questionId
    );
    
    List<UserSecurityAnswer> findByUser_UserId(Integer userId);
}
