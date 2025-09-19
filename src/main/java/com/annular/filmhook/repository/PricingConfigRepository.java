package com.annular.filmhook.repository;

import com.annular.filmhook.model.PricingConfig;
import com.annular.filmhook.model.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PricingConfigRepository extends JpaRepository<PricingConfig, Integer> {

    @Query("SELECT p FROM PricingConfig p WHERE p.serviceType = :serviceType AND p.active = true")
    Optional<PricingConfig> findActiveConfigByService(ServiceType serviceType);
}