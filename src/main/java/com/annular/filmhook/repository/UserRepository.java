package com.annular.filmhook.repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("select u from User u where u.userType='sub Admin' and u.status=true")
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

	@Query("SELECT u FROM User u WHERE " +
		       "(u.userType = :publicUserType OR " +
		       "(u.userType = :industryUserType AND u.adminReview IN :adminReviewRange)) " +
		       "AND LOWER(u.name) LIKE LOWER(CONCAT('%', :searchKey, '%')) " +
		       "AND u.status = :status")
		List<User> findByNameContainingIgnoreCaseAndStatusAndUserTypeOrAdminReviewInRange(
		       String searchKey,
		       boolean status,
		       String publicUserType,
		       String industryUserType,
		       List<Float> adminReviewRange);


	@Query("SELECT u FROM User u WHERE " +
		       "((u.userType = :publicUserType) OR " +
		       "(u.userType = :industryUserType AND u.adminReview IN :adminReviewRange)) " +
		       "OR (:loggedInUserType = :industryUserType AND u.adminReview BETWEEN 5.1 AND 9.9) " +
		       "AND LOWER(u.name) LIKE LOWER(CONCAT('%', :searchKey, '%')) " +
		       "AND u.status = :status")
		List<User> findByNameContainingIgnoreCaseAndStatusAndUserTypeOrAdminReviewInRange(
		    String searchKey,boolean status,String publicUserType,String industryUserType,List<Float> adminReviewRange,String loggedInUserType
		);



	@Query("SELECT u FROM User u WHERE u.userId = :auditionAcceptanceUser")
	Optional<User> findByIdss(Integer auditionAcceptanceUser);

	Optional<User> findByUserId(Integer userId);

    @Query("SELECT u FROM User u WHERE u.deleteReason IS NOT NULL AND u.status=true")
	List<User> findByDeleteReasonIsNotNull();

    @Query("select u from User u where u.userType=:userType")
	List<User> findByUserType(String userType);



	@Query("SELECT u FROM User u WHERE u.userType = :userType AND u.status = true")
	Page<User> findByUserTypeAndStatusTrue(@Param("userType") String userType, Pageable pageable);
	
	@Query("SELECT COUNT(u) FROM User u WHERE u.status = true AND u.userType IN ('Public User', 'Industry User') AND u.createdOn BETWEEN :startDate AND :endDate")
	int getTotalActiveUserCount(Date startDate, Date endDate);


	@Query("SELECT COUNT(u) FROM User u WHERE u.status = true AND u.userType = 'Public User' AND u.createdOn BETWEEN :startDate AND :endDate")
	int getActivePublicUserCount(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

	@Query("SELECT COUNT(u) FROM User u WHERE u.status = true AND u.userType = 'Industry User' AND u.createdOn BETWEEN :startDate AND :endDate")
	int getActiveIndustryUserCount(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

	@Query("SELECT u FROM User u WHERE u.status = true AND u.createdOn BETWEEN :startDate AND :endDate")
	Page<User> findByStatusTrueAndCreatedOnBetween(@Param("startDate") Date startDate,
	                                               @Param("endDate") Date endDate,
	                                               Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.status = true AND u.userType = :userType AND u.createdOn BETWEEN :startDate AND :endDate")
	Page<User> findByUserTypeAndStatusTrueAndCreatedOnBetween(@Param("userType") String userType,
	                                                           @Param("startDate") Date startDate,
	                                                           @Param("endDate") Date endDate,
	                                                           Pageable pageable);

//	@Query("SELECT u FROM User u WHERE u.industryUserVerified = :status OR (u.industryUserVerified IS NOT NULL AND u.status = true)")
//	Page<User> findUnverifiedOrRejectedUsers(Boolean status,Pageable pageable);
	
	@Query("SELECT u FROM User u WHERE (u.industryUserVerified = :status OR (:status IS NULL AND u.industryUserVerified IS NULL)) AND u.status = true")
	Page<User> findUnverifiedOrRejectedUsers(@Param("status") Boolean status, Pageable pageable);

	
	@Query("SELECT COUNT(u) FROM User u WHERE (u.notificationCount IS NULL OR u.notificationCount = false) AND u.status = true")
	Integer countByNotificationCountIsNullOrNotificationCountFalseAndStatusTrue();


	@Query("SELECT u FROM User u WHERE u.email = :email AND u.status = true")
	Optional<User> findActiveUserByEmail(@Param("email") String email);
	List<User> findByFilmHookCodeContainingIgnoreCaseAndStatus(String filmHookCode, Boolean status);

	@Query("SELECT u FROM User u WHERE u.email = :email AND u.status = false")
	Optional<User> findInactiveUserByEmail(@Param("email") String email);






	
	






}
