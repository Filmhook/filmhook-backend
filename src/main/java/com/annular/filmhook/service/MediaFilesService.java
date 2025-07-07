package com.annular.filmhook.service;

import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.User;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;

import java.util.List;

public interface MediaFilesService {

    List<FileOutputWebModel> saveMediaFiles(FileInputWebModel fileInputWebModel, User user);

    List<FileOutputWebModel> getMediaFilesByUserId(Integer userId);

    List<FileOutputWebModel> getMediaFilesByCategory(MediaFileCategory category);

    List<FileOutputWebModel> getMediaFilesByCategoryAndUserId(MediaFileCategory category, Integer userId);

    List<FileOutputWebModel> getMediaFilesByCategoryAndRefId(MediaFileCategory category, Integer refId);

    List<FileOutputWebModel> getMediaFilesByUserIdAndCategoryAndRefId(Integer userId, MediaFileCategory category, Integer refId);

    void deleteMediaFilesByUserIdAndCategoryAndRefIds(Integer userId, MediaFileCategory category, List<Integer> idList);

    void deleteMediaFilesByCategoryAndRefIds(MediaFileCategory category, List<Integer> idList);
    
    
    

}
