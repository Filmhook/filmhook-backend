package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.IndustryMediaFiles;

@Repository
public interface IndustryMediaFileRepository extends JpaRepository<IndustryMediaFiles, Integer> {

    @Query("Select m from IndustryMediaFiles m where m.user.userId=:userId and m.status=true")
    List<IndustryMediaFiles> getMediaFilesByUserIdAndCategory(Integer userId);
    
    @Query("SELECT imf FROM IndustryMediaFiles imf JOIN imf.user u WHERE imf.status = true AND imf.unverifiedList IS NULL")
    Page<IndustryMediaFiles> getAllUnverifiedIndustrialUsers(Pageable paging);



//    @Query("SELECT imf FROM IndustryMediaFiles imf WHERE imf.user IS NULL OR imf.status=true AND imf.unverifiedList IS NULL")
//    List<IndustryMediaFiles> getAllUnverifiedIndustrialUsers();

    @Query("Select m from IndustryMediaFiles m where m.user.userId=:userId")
    List<IndustryMediaFiles> findByUserId(Integer userId);

    @Query("SELECT COUNT(DISTINCT imf.user.id) FROM IndustryMediaFiles imf WHERE imf.notificationCount IS NULL OR imf.notificationCount = 0")
    Integer countDistinctUsersByNotificationCountNullOrZero();


}
