package com.annular.filmhook.service;

import java.util.List;

import org.springframework.core.io.Resource;

import com.annular.filmhook.webmodel.FileInputWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;

public interface ProjectService {

	public FileOutputWebModel saveProjectFiles(FileInputWebModel inputFileData);

	public List<FileOutputWebModel> getProjectFiles(Integer userId, Integer platformPermanentId);

	public Resource getProjectFiles(Integer userId, String category, String fileId);
		

}
