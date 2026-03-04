package com.annular.filmhook.exception;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annular.filmhook.enums.VerificationType;
import com.annular.filmhook.model.UserVerificationAttempt;


public interface UserVerificationAttemptRepository extends JpaRepository<UserVerificationAttempt, Integer> {

Optional<UserVerificationAttempt> findByUser_UserIdAndVerificationType( Integer userId, VerificationType verificationType);
}
