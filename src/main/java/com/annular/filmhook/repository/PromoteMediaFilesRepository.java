package com.annular.filmhook.repository;

import com.annular.filmhook.model.PromoteMediaFiles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromoteMediaFilesRepository extends JpaRepository<PromoteMediaFiles, Integer> {

    List<PromoteMediaFiles> findByPromote_PromoteId(Integer promoteId);

    void deleteByPromote_PromoteId(Integer promoteId);
    
    List<PromoteMediaFiles> findByPromote_PromoteIdAndSelected(Integer promoteId, Boolean selected);

}