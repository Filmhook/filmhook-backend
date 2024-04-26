package com.annular.filmhook.service;

import com.annular.filmhook.model.User;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.StoriesWebModel;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface MediaFilesService {

    FileOutputWebModel saveMediaFiles(FileInputWebModel fileInputWebModel, User user);
    List<FileOutputWebModel> getMediaFilesByUser(Integer userId);
    List<FileOutputWebModel> getMediaFilesByUserAndCategory(Integer userId);
    List<FileOutputWebModel> getMediaFilesByCategoryAndRefId(String category, Integer refId);
    void deleteMediaFilesByUserIdAndCategoryAndRefId(Integer userId, String category, List<Integer> idList);
    void deleteMediaFilesByCategoryAndRefId(String category, List<Integer> idList);
	FileOutputWebModel saveMediaFiles(MultipartFile file);
	//List<FileOutputWebModel> getMediaFilesByUserAndCategory(String category);
	List<FileOutputWebModel> getMediaFilesByUserAndCategory();
	FileOutputWebModel saveMediaFiles(StoriesWebModel fileInput, User user);
	

}
