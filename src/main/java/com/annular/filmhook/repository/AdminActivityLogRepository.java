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
            "a.targetId, " +  // <-- FIX: return AdminActivityLog.targetId
            "u.name, " +
            "FUNCTION('DATE_FORMAT', a.createdOn, '%d/%m/%Y'), " +
            "a.actionType, " +
            "ac.companyName, ac.companyType, ac.verificationStatus) " +
            "FROM AdminActivityLog a " +
            "JOIN AuditionCompanyDetails ac ON ac.user.id = a.targetId " +
            "JOIN User u ON u.id = a.targetId " +
            "WHERE a.adminId = :adminId " +
            "AND a.targetType = 'AUDITION' " +
            "ORDER BY a.createdOn DESC")
    List<AuditionRowDTO> getAuditionRows(@Param("adminId") Integer adminId);


    List<AdminActivityLog> findByTargetTypeAndTargetId(
            String targetType,
            Integer targetId
    );

}
