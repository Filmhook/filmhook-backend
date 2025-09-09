package com.annular.filmhook.service;
import java.util.List;

import com.annular.filmhook.model.AuditionNewProject;

import com.annular.filmhook.webmodel.AuditionNewProjectWebModel;

public interface AuditionNewService {
	AuditionNewProject createProject(AuditionNewProjectWebModel projectDto);
	List<AuditionNewProjectWebModel> getProjectsBySubProfession(Integer subProfessionId);
	List<AuditionNewProjectWebModel> getProjectsByCompanyId(Integer companyId);
}
