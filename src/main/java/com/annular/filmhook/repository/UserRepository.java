package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	@Query("select u from User u where u.userId=:userId and u.status=true")
	Optional<User> getUserByUserId(Integer userId);

	@Query("select u from User u where u.email=:email and u.userType=:userType")
	Optional<User> findByUserName(String email, String userType);

	@Query("select u from User u where u.email=:email and u.userType=:userType")
	Optional<User> findByEmailAndUserType(String email, String userType);

	@Query("select u from User u where u.verificationCode=:code")
	User findByVerificationCode(String code);

	@Query("select u from User u where u.email=:email and u.status=true")
	Optional<User> findByEmail(String email);
	
	@Query("select u from User u where u.email=:email and u.userType=:userType")
	Optional<User> findByEmail(String email, String userType);

	@Query("select u from User u where u.email=:email")
	Optional<User> findByEmailId(String email);

	@Query("select u from User u where u.name=:name")
	Optional<User> findByUserNameType(String name);

	@Query("select u from User u where u.email=:email")
	Optional<User> findByAllUserEmailId(String email);

	@Query("select u from User u where u.otp=:otp and u.phoneNumber=:phoneNumber")
	Optional<User> findByOtp(Integer otp, String phoneNumber);
	


}
