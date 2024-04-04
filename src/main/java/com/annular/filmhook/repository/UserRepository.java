package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	@Query("select u from User u where u.userId=:userId")
	Optional<User> getUserByUserId(Integer userId);

	@Query("select u from User u where u.email=:email and u.userType=:userType and u.status=true")
	Optional<User> findByEmailAndUserType(String email, String userType);

	@Query("select u from User u where u.verificationCode=:code")
	User findByVerificationCode(String code);

	@Query("select u from User u where u.email=:email")
	Optional<User> findByEmail(String email);

	@Query("select u from User u where u.name=:name and u.userType=:userType")
	Optional<User> findByNameAndUserType(String name, String userType);

	@Query("select u from User u where u.verificationCode = :verificationCode and u.phoneNumber = :phoneNumber")
	Optional<User> findByOtp(String verificationCode, String phoneNumber);

	@Query("select u from User u where u.userId=:id")
	Optional<User> findByResetPassword(Integer id);

	@Query("select u from User u where u.phoneNumber=:phoneNumber and u.userType=:userType")
	Optional<User> findByPhoneNumberAndUserType(String phoneNumber, String userType);

	@Query("select u from User u where u.email=:email and u.userType=:userType and u.status=true and u.mobileNumberStatus=true")
	Optional<User> findByEmailAndUserTypeAndMobile(String email, String userType);

	@Query("select u from User u where u.otp = :otp and u.userId = :userId")
	Optional<User> findByOTps(Integer otp, Integer userId);


}
