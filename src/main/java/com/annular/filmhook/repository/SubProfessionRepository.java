package com.annular.filmhook.repository;

import com.annular.filmhook.model.Profession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.annular.filmhook.model.SubProfession;

import java.util.List;

@Repository
public interface SubProfessionRepository extends JpaRepository<SubProfession, Integer> {

    @Query("Select p From SubProfession p Where p.profession in (:professionIds) and p.status = true")
    List<SubProfession> getSubProfessionByProfessionIds(List<Profession> professionIds);

    SubProfession findBySubProfessionName(String subProfessionName);
}
