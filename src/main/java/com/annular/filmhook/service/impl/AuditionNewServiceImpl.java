package com.annular.filmhook.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.annular.filmhook.UserDetails;
import com.annular.filmhook.converter.AuditionCompanyConverter;
import com.annular.filmhook.model.AuditionCompanyDetails;
import com.annular.filmhook.model.AuditionNewProject;
import com.annular.filmhook.model.AuditionNewTeamNeed;
import com.annular.filmhook.model.MediaFileCategory;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.AuditionCompanyRepository;
import com.annular.filmhook.repository.AuditionNewTeamNeedRepository;
import com.annular.filmhook.repository.AuditionProjectRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.AuditionNewService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.webmodel.AuditionNewProjectWebModel;
import com.annular.filmhook.webmodel.AuditionNewTeamNeedWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;

@Service
public class AuditionNewServiceImpl implements AuditionNewService {

    @Autowired
    private AuditionProjectRepository projectRepository;
    
    @Autowired
    private AuditionNewTeamNeedRepository teamNeedRepository;
    
    @Autowired
    private AuditionCompanyRepository companyRepository;
    
    @Autowired
    private  UserDetails userDetails;
    
    @Autowired
   private UserRepository userRepository;
    
    @Autowired
    private MediaFilesService mediaFilesService;
    
    @Override
    public AuditionNewProject createProject(AuditionNewProjectWebModel projectDto) {

        // ✅ Get currently logged-in user's ID
        Integer userId = userDetails.userInfo().getId();

        // ✅ Fetch User entity
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // ✅ Find the company
        AuditionCompanyDetails company = companyRepository.findById(projectDto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found with ID: " + projectDto.getCompanyId()));

        // ✅ Convert DTO → Entity (with userId)
        AuditionNewProject project = AuditionCompanyConverter.toEntity(projectDto, company, userId);

        // ✅ Save project
        AuditionNewProject savedProject = projectRepository.save(project);

        // ✅ Handle profile picture upload
        AuditionCompanyConverter.handleProjectProfilePictureFile(projectDto, savedProject, user, mediaFilesService);

        return savedProject;
    }

    @Override
    public List<AuditionNewProjectWebModel> getProjectsBySubProfession(Integer subProfessionId) {
        List<AuditionNewTeamNeed> teamNeeds = teamNeedRepository.findAllBySubProfessionId(subProfessionId);

        return teamNeeds.stream()
                .filter(teamNeed -> Boolean.TRUE.equals(teamNeed.getStatus()))
                .map(AuditionNewTeamNeed::getProject)
                .distinct()
                .map(project -> {
                    // Convert entity → DTO
                    AuditionNewProjectWebModel dto = AuditionCompanyConverter.toDto(project);

                    // 🔹 Fetch profile pictures for this project
                    List<FileOutputWebModel> profilePictures = mediaFilesService
                            .getMediaFilesByCategoryAndRefId(
                                    MediaFileCategory.AuditionProfilePicture,
                                    project.getId()
                            );
                    if (profilePictures != null && !profilePictures.isEmpty()) {
                        dto.setProfilePictureFilesOutput(profilePictures);
                    }

                    // 🔹 Fetch company logo for this project’s company
                    AuditionCompanyDetails company = project.getCompany();
                    if (company != null) {
                        List<FileOutputWebModel> companyLogos = mediaFilesService
                                .getMediaFilesByCategoryAndRefId(
                                        MediaFileCategory.Audition,
                                        company.getId()
                                );
                        if (companyLogos != null && !companyLogos.isEmpty()) {
                            dto.setLogoFiles(companyLogos);
                        }
                    }

                    // ✅ Only include active teamNeeds for this subProfessionId
                    List<AuditionNewTeamNeedWebModel> activeTeamNeeds = project.getTeamNeeds().stream()
                            .filter(tn -> Boolean.TRUE.equals(tn.getStatus()))
                            .filter(tn -> tn.getSubProfession() != null
                                       && tn.getSubProfession().getSubProfessionId().equals(subProfessionId))
                            .map(AuditionCompanyConverter::toDto)
                            .collect(Collectors.toList());

                    dto.setTeamNeeds(activeTeamNeeds);

                    return dto;
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<AuditionNewProjectWebModel> getProjectsByCompanyId(Integer companyId) {
        // Fetch projects belonging to this company
        List<AuditionNewProject> projects = projectRepository.findAllByCompanyId(companyId);

        // 🔹 Fetch company logo 
        List<FileOutputWebModel> companyLogos = mediaFilesService
                .getMediaFilesByCategoryAndRefId(
                        MediaFileCategory.Audition, 
                        companyId
                );

        return projects.stream()
                .map(project -> {
                    // Fetch teamNeeds for this project where status = true
                    List<AuditionNewTeamNeed> activeTeamNeeds = teamNeedRepository.findAllByProjectId(project.getId())
                            .stream()
                            .filter(teamNeed -> Boolean.TRUE.equals(teamNeed.getStatus()))
                            .collect(Collectors.toList());

                    // If no active teamNeeds → skip this project
                    if (activeTeamNeeds.isEmpty()) {
                        return null;
                    }

                    // Convert entity → DTO
                    AuditionNewProjectWebModel dto = AuditionCompanyConverter.toDto(project);

                    // Set only active teamNeeds
                    dto.setTeamNeeds(
                            activeTeamNeeds.stream()
                                    .map(AuditionCompanyConverter::toDto)
                                    .collect(Collectors.toList())
                    );

                    // 🔹 Set profile pictures for project
                    List<FileOutputWebModel> profilePictures = mediaFilesService
                            .getMediaFilesByCategoryAndRefId(
                                    MediaFileCategory.AuditionProfilePicture,
                                    project.getId()
                            );

                    if (profilePictures != null && !profilePictures.isEmpty()) {
                        dto.setProfilePictureFilesOutput(profilePictures);
                    }

                    // 🔹 Attach company logo (same for all projects in this company)
                    if (companyLogos != null && !companyLogos.isEmpty()) {
                        dto.setLogoFiles(companyLogos); // 👈 add this field in DTO
                    }

                    return dto;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


}
