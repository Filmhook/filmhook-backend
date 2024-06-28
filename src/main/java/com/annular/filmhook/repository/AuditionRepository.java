package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.Audition;

@Repository
public interface AuditionRepository extends JpaRepository<Audition, Integer> {

    List<Audition> findByAuditionCategory(Integer categoryId);

    @Query("SELECT a FROM Audition a WHERE a.auditionTitle = :auditionTitle")
    List<Audition> findByAuditionTitle(String auditionTitle);

}
