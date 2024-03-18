package com.annular.filmhook.service;

import com.annular.filmhook.model.User;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;

import java.util.List;

public interface MediaFilesService {

    FileOutputWebModel saveMediaFiles(FileInputWebModel fileInputWebModel, User user);
    List<FileOutputWebModel> getMediaFilesByUser(Integer userId);
    List<FileOutputWebModel> getMediaFilesByUserAndCategory(Integer userId, String category);
    void deleteMediaFilesByUserIdAndCategory(Integer userId, String category);

}
