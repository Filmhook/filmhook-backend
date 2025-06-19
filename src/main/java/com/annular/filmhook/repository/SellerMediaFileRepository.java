package com.annular.filmhook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annular.filmhook.model.SellerMediaFile;


public interface SellerMediaFileRepository extends JpaRepository<SellerMediaFile, Integer> {

	List<SellerMediaFile> findBySellerId(Long sellerId);


}
