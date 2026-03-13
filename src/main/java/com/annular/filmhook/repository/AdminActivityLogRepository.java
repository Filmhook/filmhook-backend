package com.annular.filmhook.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.AdminActivityLog;
import com.annular.filmhook.webmodel.AdminUserRowDTO;
import com.annular.filmhook.webmodel.AuditionRowDTO;
import com.annular.filmhook.webmodel.ShootingLocationRowDTO;

@Repository
public interface AdminActivityLogRepository extends JpaRepository<AdminActivityLog, Integer> {

	 // Count actions done today (Work Done)
    @Query("SELECT COUNT(a) FROM AdminActivityLog a " +
           "WHERE a.adminId = :adminId " +
           "AND DATE(a.createdOn) = CURRENT_DATE")
    int countTodayActivities(Integer adminId);

    // Fetch first activity of the day
    @Query("SELECT MIN(a.createdOn) FROM AdminActivityLog a " +
           "WHERE a.adminId = :adminId " +
           "AND DATE(a.createdOn) = CURRENT_DATE")
    LocalDateTime getFirstActivityOfDay(Integer adminId);

    // Fetch last activity of the day
    @Query("SELECT MAX(a.createdOn) FROM AdminActivityLog a " +
           "WHERE a.adminId = :adminId " +
           "AND DATE(a.createdOn) = CURRENT_DATE")
    LocalDateTime getLastActivityOfDay(Integer adminId);
    
    @Query("SELECT new com.annular.filmhook.webmodel.AdminUserRowDTO(" +
            "u.id, u.name, " +
            "FUNCTION('DATE_FORMAT', a.createdOn, '%d/%m/%Y'), " +
            "a.actionType) " +
            "FROM AdminActivityLog a " +
            "JOIN User u ON u.id = a.targetId " +
            "WHERE a.adminId = :adminId " +
            "AND a.targetType = 'INDUSTRY_USER' " +
            "ORDER BY a.createdOn DESC")
    List<AdminUserRowDTO> getIndustryUserRows(@Param("adminId") Integer adminId);

    
    @Query("SELECT new com.annular.filmhook.webmodel.AuditionRowDTO(" +
            "ac.id, " +
            "u.name, " +
            "FUNCTION('DATE_FORMAT', a.createdOn, '%d/%m/%Y'), " +
            "a.actionType, " +
            "ac.companyName, ac.companyType, ac.govtVerified, ac.verificationStatus) " +
            "FROM AdminActivityLog a " +
            "JOIN AuditionCompanyDetails ac ON ac.id = a.targetId " +
            "JOIN User u ON u.id = ac.user.id " +
            "WHERE a.adminId = :adminId " +
            "AND a.targetType = 'AUDITION' " +
            "ORDER BY a.createdOn DESC")
    List<AuditionRowDTO> getAuditionRows(@Param("adminId") Integer adminId);


    List<AdminActivityLog> findByTargetTypeAndTargetId(
            String targetType,
            Integer targetId
    );
    @Query("SELECT new com.annular.filmhook.webmodel.ShootingLocationRowDTO(" +
            "p.id, " +
            "u.name, " +
            "p.propertyName, " +
            "c.name, " +
            "CASE " +
            " WHEN COALESCE(s.entireProperty,false) = true AND COALESCE(s.singleProperty,false) = true THEN 'Entire & Single Property' " +
            " WHEN COALESCE(s.entireProperty,false) = true THEN 'Entire Property' " +
            " WHEN COALESCE(s.singleProperty,false) = true THEN 'Single Property' " +
            " ELSE 'N/A' END, " +
            "u.userType, " +
            "p.location, " +
            "FUNCTION('DATE_FORMAT', a.createdOn,'%d/%m/%Y'), " +
            "a.actionType) " +  
            "FROM AdminActivityLog a " +
            "JOIN ShootingLocationPropertyDetails p ON p.id = a.targetId " +
            "JOIN p.user u " +
            "LEFT JOIN p.category c " +
            "LEFT JOIN p.subcategorySelection s " +
            "WHERE a.adminId = :adminId " +
            "AND a.targetType = 'SHOOTING_LOCATION' " +
            "ORDER BY a.createdOn DESC")
    List<ShootingLocationRowDTO> getShootingLocationRows(@Param("adminId") Integer adminId);
}
