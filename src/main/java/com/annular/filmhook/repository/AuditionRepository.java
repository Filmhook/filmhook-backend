package com.annular.filmhook.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Audition;

@Repository
public interface AuditionRepository extends JpaRepository<Audition, Integer> {

	@Query("SELECT a FROM Audition a WHERE a.auditionCategory = :categoryId AND a.auditionIsactive = true")
    List<Audition> findByAuditionCategory(Integer categoryId);

    @Query("SELECT a FROM Audition a WHERE a.auditionTitle = :auditionTitle")
    List<Audition> findByAuditionTitle(String auditionTitle);

	List<Audition> findByAuditionIsactiveTrueAndEndDateBefore(LocalDate today);


    // Fetch only active auditions with endDate before or equal to today
    @Query("SELECT a FROM Audition a WHERE a.auditionIsactive = true AND a.endDate <= CURRENT_DATE")
	List<Audition> findByAuditionIsactiveTrue();

}
