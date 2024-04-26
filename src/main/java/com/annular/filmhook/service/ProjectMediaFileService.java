package com.annular.filmhook.service;

import java.util.List;

import com.annular.filmhook.model.User;
import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;

public interface ProjectMediaFileService {

	FileOutputWebModel saveMediaFiles(FileInputWebModel inputFileData, User user);

	List<FileOutputWebModel> getMediaFilesByUserAndplatformPermanentId(Integer userId, Integer platformPermanentId);

}
