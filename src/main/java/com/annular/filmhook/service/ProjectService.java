package com.annular.filmhook.service;

import java.util.List;

import org.springframework.core.io.Resource;

import com.annular.filmhook.webmodel.ProjectWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;

public interface ProjectService {

    List<FileOutputWebModel> saveProjectFiles(ProjectWebModel projectWebModel);

    List<FileOutputWebModel> getProjectFiles(Integer userId, Integer platformPermanentId);

    Resource getProjectFiles(Integer userId, String category, String fileId);
    
    List<FileOutputWebModel> getPendingProjectFiles(Integer userId, Integer platformPermanentId);
    boolean updateProjectFileStatus(Integer fileId, String status);

}
