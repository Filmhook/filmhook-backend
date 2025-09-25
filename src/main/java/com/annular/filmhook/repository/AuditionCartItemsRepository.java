package com.annular.filmhook.repository;

import com.annular.filmhook.model.AuditionCartItems;
import com.annular.filmhook.model.User;
import com.annular.filmhook.model.FilmSubProfession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuditionCartItemsRepository extends JpaRepository<AuditionCartItems, Integer> {

    // ✅ Use entity references instead of primitive IDs
    Optional<AuditionCartItems> findByUserAndCompanyIdAndSubProfession(User user, Integer companyId, FilmSubProfession subProfession);

    List<AuditionCartItems> findByUserAndCompanyId(User user, Integer companyId);
}