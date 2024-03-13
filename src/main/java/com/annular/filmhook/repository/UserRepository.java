package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	@Query("select u from User u where u.name=:name and u.userType=:userType")
	Optional<User> findByUserName(String name, String userType);

	@Query("select u from User u where u.email=:email and u.userType=:userType")
	Optional<User> findByEmail(String email, String userType);

	@Query("select u from User u where u.verificationCode=:code")
	User findByVerificationCode(String code);

	@Query("select u from User u where u.email=:email and u.status=true and u.userType=:userType")
	Optional<User> existByUserName(String email, String userType);

	@Query("select u from User u where u.email=:email")
	Optional<User> findByEmailAndUserType(String email);

}
