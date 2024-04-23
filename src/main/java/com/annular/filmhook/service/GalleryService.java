package com.annular.filmhook.service;

import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

public interface GalleryService {

    FileOutputWebModel saveGalleryFiles(FileInputWebModel fileInputWebModel);
    List<FileOutputWebModel> getGalleryFilesByUser(Integer userId) throws IOException;
    Resource getGalleryFile(Integer userId, String category, String fileId);
	Resource getAllGalleryFilesInCategory(Integer  userId,String category);
	Resource getAllGalleryFilesInCategory(String category);
	List<FileOutputWebModel> getGalleryFilesByAllUser();
}
