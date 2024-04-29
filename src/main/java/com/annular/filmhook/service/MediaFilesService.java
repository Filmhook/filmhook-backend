package com.annular.filmhook.service;

import com.annular.filmhook.model.User;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;

import java.util.List;

public interface MediaFilesService {

    List<FileOutputWebModel> saveMediaFiles(FileInputWebModel fileInputWebModel, User user);
    List<FileOutputWebModel> getMediaFilesByUserId(Integer userId);
    List<FileOutputWebModel> getMediaFilesByCategory(String category);
    List<FileOutputWebModel> getMediaFilesByCategoryAndUserId(String category, Integer userId);
    List<FileOutputWebModel> getMediaFilesByCategoryAndRefId(String category, Integer refId);
    List<FileOutputWebModel> getMediaFilesByUserIdAndCategoryAndRefId(Integer userId, String category, Integer refId);
    void deleteMediaFilesByUserIdAndCategoryAndRefId(Integer userId, String category, List<Integer> idList);
    void deleteMediaFilesByCategoryAndRefId(String category, List<Integer> idList);

}
