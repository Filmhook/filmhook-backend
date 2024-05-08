package com.annular.filmhook.repository;

import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.MediaFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaFilesRepository extends JpaRepository<MediaFiles, Integer> {

    @Query("Select m from MediaFiles m where m.user.userId=:userId and m.status=true")
    List<MediaFiles> getMediaFilesByUserId(Integer userId);

    @Query("Select m from MediaFiles m where m.user.userId=:userId and m.category=:category and m.status=true")
    List<MediaFiles> getMediaFilesByUserIdAndCategory(Integer userId, MediaFileCategory category);

    @Query("Select m from MediaFiles m where m.user.userId=:userId and m.category=:category and m.categoryRefId IN (:refIds) and m.status=true")
    List<MediaFiles> getMediaFilesByUserIdAndCategoryAndRefIds(Integer userId, MediaFileCategory category, List<Integer> refIds);

    @Query("Select m from MediaFiles m where m.user.userId=:userId and m.category=:category and m.categoryRefId = :refId and m.status=true")
    List<MediaFiles> getMediaFilesByUserIdAndCategoryAndRefId(Integer userId, MediaFileCategory category, Integer refId);

    @Query("Select m from MediaFiles m where m.category=:category and m.categoryRefId=:refId and m.status=true")
    List<MediaFiles> getMediaFilesByCategoryAndRefId(MediaFileCategory category, Integer refId);

    @Query("Select m from MediaFiles m where m.category=:category and m.categoryRefId IN (:refIds) and m.status=true")
    List<MediaFiles> getMediaFilesByCategoryAndRefIds(MediaFileCategory category, List<Integer> refIds);

    @Query("Select m from MediaFiles m where m.category = :category and m.status=true")
    List<MediaFiles> getMediaFilesByCategory(MediaFileCategory category);

    @Query("SELECT m FROM MediaFiles m WHERE m.user.userId = :userId AND m.category = :profilepic AND m.status = true")
    MediaFiles findByUserUserIdAndCategory(Integer userId, MediaFileCategory profilepic);

    @Query("select m from MediaFiles m where m.user.userId = :userId and m.category = :profilepic ")
	MediaFiles getProfilePicByUserId(Integer userId);

    @Query("select m from MediaFiles m where m.id = :pinMediaId")
	Optional<MediaFiles> findByIds(Integer pinMediaId);
}
