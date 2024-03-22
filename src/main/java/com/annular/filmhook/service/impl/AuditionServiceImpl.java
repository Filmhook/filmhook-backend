package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.Audition;
import com.annular.filmhook.model.AuditionRoles;
import com.annular.filmhook.repository.AuditionRepository;
import com.annular.filmhook.repository.AuditionRolesRepository;
import com.annular.filmhook.service.AuditionService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;
import com.annular.filmhook.util.FileUtil;
import com.annular.filmhook.webmodel.AuditionWebModel;

@Service
public class AuditionServiceImpl implements AuditionService {

	public static final Logger logger = LoggerFactory.getLogger(AuditionServiceImpl.class);
	
	@Autowired
    MediaFilesService mediaFilesService;

    @Autowired
    FileUtil fileUtil;

    @Autowired
    UserService userService;

    @Autowired
    AuditionRepository auditionRepository;
    
    @Autowired
    AuditionRolesRepository auditionRolesRepository;
    
	@Override
	public ResponseEntity<?> saveAudition(AuditionWebModel auditionWebModel) {
		HashMap<String, Object> response = new HashMap<String, Object>();
		try {
			logger.info("Save audition method start");
			
		    Audition audition = new Audition();
		    
		    AuditionRoles auditionRoles = new AuditionRoles();
			
			audition.setAuditionTitle(auditionWebModel.getAuditionTitle());
			audition.setAuditionExperience(auditionWebModel.getAuditionExperience());
			audition.setAuditionCategory(auditionWebModel.getAuditionCategory());
			audition.setAuditionExpireOn(auditionWebModel.getAuditionExpireOn());
			audition.setAuditionPostedBy(auditionWebModel.getAuditionPostedBy());
			audition.setAuditionCreatedBy(auditionWebModel.getAuditionPostedBy());
			audition.setAuditionIsactive(true);
			
			Audition savedAudition = auditionRepository.save(audition);
			List<AuditionRoles> auditionRolesList = new ArrayList<>();
			
			if(auditionWebModel.getAuditionRoles().length != 0) {
				String auditionRolesA[] = auditionWebModel.getAuditionRoles();
				for (String role : auditionRolesA) {
					
					auditionRoles.setAuditionRoleDesc(role);
					auditionRoles.setAuditionReferenceId(savedAudition.getAuditionId());
					auditionRoles.setAuditionRoleCreatedBy(savedAudition.getAuditionCreatedBy());
					auditionRoles.setAuditionRoleIsactive(true);
					
					auditionRolesList.add(auditionRolesRepository.save(auditionRoles));
					
				}
			}
			
			response.put("Audition details", savedAudition);
			response.put("Audition roles", auditionRolesList);
			
		} catch (Exception e) {
			logger.error("Save audition Method Exception...", e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Fail", e.getMessage()));
		}
		return ResponseEntity.status(HttpStatus.OK).body(new Response(1, "Audition details saved successfully", response));	
	}



}
