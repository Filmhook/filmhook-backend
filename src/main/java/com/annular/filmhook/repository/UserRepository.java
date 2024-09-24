package com.annular.filmhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("select u from User u where u.userId=:userId and u.status=true")
    Optional<User> getUserByUserId(Integer userId);

    @Query("select u from User u where u.email=:email and u.userType=:userType and u.status=true")
    Optional<User> findByEmailAndUserType(String email, String userType);

    @Query("select u from User u where u.verificationCode=:code")
    User findByVerificationCode(String code);

    @Query("select u from User u where u.email=:email and u.status=true")
    Optional<User> findByEmail(String email);

    @Query("select u from User u where u.name=:name and u.userType=:userType")
    Optional<User> findByNameAndUserType(String name, String userType);

    @Query("select u from User u where u.verificationCode = :verificationCode and u.phoneNumber = :phoneNumber")
    Optional<User> findByVerificationCodeAndPhoneNumber(String verificationCode, String phoneNumber);

    @Query("select u from User u where u.phoneNumber=:phoneNumber and u.userType=:userType")
    Optional<User> findByPhoneNumberAndUserType(String phoneNumber, String userType);

    @Query("select u from User u where u.userType='subAdmin' and u.status=true")
    Page<User> findByUserType(String userType, Pageable paging);
    
    @Query("select u from User u where u.otp =otp")
    List<User> findByOtp(Integer otp);

    @Query("select u from User u where u.forgotOtp=:forgotOtp")
    Optional<User> findByResetPasswords(String forgotOtp);

    @Query("select u from User u where u.email=:email and u.status=true and u.adminPageStatus=true")
    Optional<User> findByEmailAndAdminStatus(String email);

    Optional<User> findByFilmHookCode(String filmHookCode);

    Optional<User> findByPhoneNumber(String newPhoneNumber);

    List<User> findByNameContainingIgnoreCaseAndStatus(String name, Boolean status);

    @Query("select u from User u where u.id!=:loggedInUserId and u.status=true")
    List<User> getAllActiveUsersExceptCurrentUser(Integer loggedInUserId);

    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :searchKey, '%')) AND u.status = true")
	List<User> getActiveUserByName(String searchKey);

    @Query("SELECT u FROM User u WHERE u.status=true")
	List<User> findAllActiveUsers();

	Optional<User> getByUserId(Integer senderId);

}
