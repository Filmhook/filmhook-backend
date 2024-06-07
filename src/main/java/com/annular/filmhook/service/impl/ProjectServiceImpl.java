package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.ProjectService;
import com.annular.filmhook.webmodel.ProjectWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.annular.filmhook.model.PlatformPermanentDetail;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.PlatformPermanentDetailRepository;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.webmodel.FileOutputWebModel;

@Service
public class ProjectServiceImpl implements ProjectService {

    public static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Autowired
    FileUtil fileUtil;

    @Autowired
    UserService userService;

    @Autowired
    PlatformPermanentDetailRepository platformPermanentDetailRepository;

    @Autowired
    MediaFilesService mediaFilesService;

    @Override
    public List<FileOutputWebModel> saveProjectFiles(ProjectWebModel projectWebModel) {
        List<FileOutputWebModel> outputList = new ArrayList<>();
        try {
            Optional<User> userFromDB = userService.getUser(projectWebModel.getUserId());
            Optional<PlatformPermanentDetail> platformFromDB = platformPermanentDetailRepository.findById(projectWebModel.getPlatformPermanentId());
            if (userFromDB.isPresent() && platformFromDB.isPresent()) {
                // Saving project media files in MySQL
                projectWebModel.getFileInputWebModel().setCategory(MediaFileCategory.Project);
                projectWebModel.getFileInputWebModel().setCategoryRefId(platformFromDB.get().getPlatformPermanentId());
                //projectWebModel.getFileInputWebModel().setDescription(null);
           
                //return mediaFilesService.saveMediaFiles(projectWebModel.getFileInputWebModel(), userFromDB.get());
                List<FileOutputWebModel> savedFiles = mediaFilesService.saveMediaFiles(projectWebModel.getFileInputWebModel(), userFromDB.get());
               // PlatformPermanentDetail platformPermanentDetail = platformFromDB.get();
                //int currentFilmCount = platformPermanentDetail.getFilmCount();
                //platformPermanentDetail.setFilmCount(currentFilmCount + savedFiles.size());

                // Save the updated film count back to the database
               // platformPermanentDetailRepository.save(platformPermanentDetail);
                
                // Add saved files to the output list
                outputList.addAll(savedFiles);
                PlatformPermanentDetail platformPermanentDetail = platformFromDB.get();
                Integer filmCount = platformPermanentDetail.getFilmCount();
                int currentFilmCount = (filmCount != null) ? filmCount : 0;
                platformPermanentDetail.setFilmCount(currentFilmCount + savedFiles.size());
                System.out.println(">>>>>>>>>>>>>>>" + (currentFilmCount + savedFiles.size()));
                // Save the updated film count back to the database
                platformPermanentDetailRepository.save(platformPermanentDetail);

                
            }
        } catch (Exception e) {
            logger.error("Error at saveProjectFiles(): ", e);
            e.printStackTrace();
        }
        return outputList;

    }

    @Override
    public List<FileOutputWebModel> getProjectFiles(Integer userId, Integer platformPermanentId) {
        List<FileOutputWebModel> outputWebModelList = new ArrayList<>();
        try {
            outputWebModelList = mediaFilesService.getMediaFilesByUserIdAndCategoryAndRefId(userId, MediaFileCategory.Project, platformPermanentId);
        } catch (Exception e) {
            logger.error("Error at getProjectFiles()...", e);
            e.printStackTrace();
        }
        return outputWebModelList;
    }

    @Override
    public Resource getProjectFiles(Integer userId, String category, String fileId) {
        try {
            Optional<User> userFromDB = userService.getUser(userId);
            if (userFromDB.isPresent()) {
                String filePath = FileUtil.generateFilePath(userFromDB.get(), category, fileId);
                return new ByteArrayResource(fileUtil.downloadFile(filePath));
            }
        } catch (Exception e) {
            logger.error("Error at getProjectFiles()...", e);
            e.printStackTrace();
        }
        return null;
    }
}