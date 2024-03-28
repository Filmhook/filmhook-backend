package com.annular.filmhook.service;

import com.annular.filmhook.model.User;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.IndustryFileInputWebModel;

public interface UserMediaFilesService {

	FileOutputWebModel saveMediaFiles(IndustryFileInputWebModel inputFileData, User user);

}
