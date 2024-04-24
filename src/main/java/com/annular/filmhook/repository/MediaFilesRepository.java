package com.annular.filmhook.repository;

import com.annular.filmhook.model.MediaFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaFilesRepository extends JpaRepository<MediaFiles, Integer> {

    @Query("Select m from MediaFiles m where m.user.userId=:userId and m.status=true")
    List<MediaFiles> getMediaFilesByUserId(Integer userId);

    @Query("Select m from MediaFiles m where m.user.userId=:userId and m.category=:category and m.status=true")
    List<MediaFiles> getMediaFilesByUserIdAndCategory(Integer userId, String category);

    @Query("Select m from MediaFiles m where m.user.userId=:userId and m.category=:category and m.categoryRefId IN (:refIds) and m.status=true")
    List<MediaFiles> getMediaFilesByUserIdAndCategoryAndRefId(Integer userId, String category, List<Integer> refIds);

    @Query("Select m from MediaFiles m where m.category=:category and m.categoryRefId=:refId and m.status=true")
    List<MediaFiles> getMediaFilesByCategoryAndRefId(String category, Integer refId);

    @Query("Select m from MediaFiles m where m.category=:category and m.categoryRefId IN (:refIds) and m.status=true")
    List<MediaFiles> getMediaFilesByCategoryAndRefId(String category, List<Integer> refIds);

    @Query("Select m from MediaFiles m where  m.category=:category and m.status=true")
	List<MediaFiles> getMediaFilesByUserIdAndCategory(String category);

    @Query("Select m from MediaFiles m where m.user.userId=:userId  and m.status=true")
	List<MediaFiles> getMediaFilesByUserIdAndCategory(Integer userId);


}
