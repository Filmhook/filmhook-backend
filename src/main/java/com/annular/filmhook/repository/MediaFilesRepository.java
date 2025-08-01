package com.annular.filmhook.repository;

import com.annular.filmhook.model.FileStatus;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.MediaFiles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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

	List<MediaFiles> findByCategoryRefId(Integer chatId);
	  @Query("SELECT m FROM MediaFiles m WHERE m.user.id = :userId AND m.category = :category AND m.categoryRefId = :refId AND m.fileStatus = :status")
	    List<MediaFiles> getMediaFilesByUserIdAndCategoryAndRefIdAndStatus(@Param("userId") Integer userId,
	                                                                        @Param("category") MediaFileCategory category,
	                                                                        @Param("refId") Integer refId,
	                                                                        @Param("status") FileStatus status);
	  List<MediaFiles> findByCategoryAndFileStatus(String category, FileStatus fileStatus);
	
	  List<MediaFiles> findByUser_UserIdAndCategoryAndFileStatus(
			    Integer userId,
			    MediaFileCategory category,
			    FileStatus fileStatus
			);


}
