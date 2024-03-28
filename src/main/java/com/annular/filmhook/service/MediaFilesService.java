package com.annular.filmhook.service;

import com.annular.filmhook.model.User;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.IndustryFileInputWebModel;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface MediaFilesService {

    FileOutputWebModel saveMediaFiles(FileInputWebModel fileInputWebModel, User user);
    List<FileOutputWebModel> getMediaFilesByUser(Integer userId);
    List<FileOutputWebModel> getMediaFilesByUserAndCategory(Integer userId, String category);
    void deleteMediaFilesByUserIdAndCategory(Integer userId, String category);
	FileOutputWebModel saveMediaFiles(MultipartFile file);
	

}
