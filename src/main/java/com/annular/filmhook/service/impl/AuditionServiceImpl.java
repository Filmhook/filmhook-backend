package com.annular.filmhook.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.annular.filmhook.util.Utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;

import com.annular.filmhook.model.AddressList;
import com.annular.filmhook.model.Audition;
import com.annular.filmhook.model.AuditionAcceptanceDetails;
import com.annular.filmhook.model.AuditionDetails;
import com.annular.filmhook.model.AuditionIgnoranceDetails;
import com.annular.filmhook.model.AuditionRoles;
import com.annular.filmhook.model.User;
import com.annular.filmhook.model.MediaFileCategory;

import com.annular.filmhook.repository.AddressListRepository;
import com.annular.filmhook.repository.AuditionAcceptanceRepository;
import com.annular.filmhook.repository.AuditionDetailsRepository;
import com.annular.filmhook.repository.AuditionIgnoranceRepository;
import com.annular.filmhook.repository.AuditionRepository;
import com.annular.filmhook.repository.AuditionRolesRepository;

import com.annular.filmhook.service.AuditionService;
import com.annular.filmhook.service.MediaFilesService;
import com.annular.filmhook.service.UserService;

import com.annular.filmhook.webmodel.AddressListWebModel;
import com.annular.filmhook.webmodel.AuditionAcceptanceWebModel;
import com.annular.filmhook.webmodel.AuditionDetailsWebModel;
import com.annular.filmhook.webmodel.AuditionIgnoranceWebModel;
import com.annular.filmhook.webmodel.AuditionRolesWebModel;
import com.annular.filmhook.webmodel.AuditionWebModel;
import com.annular.filmhook.webmodel.FileOutputWebModel;

@Service
public class AuditionServiceImpl implements AuditionService {

    public static final Logger logger = LoggerFactory.getLogger(AuditionServiceImpl.class);

    @Autowired
    MediaFilesService mediaFilesService;

    @Autowired
    UserService userService;

    @Autowired
    AuditionRepository auditionRepository;

    @Autowired
    AuditionRolesRepository auditionRolesRepository;

    @Autowired
    AuditionAcceptanceRepository acceptanceRepository;

    @Autowired
    AuditionDetailsRepository auditionDetailsRepository;

    @Autowired
    AddressListRepository addressListRepository;

    @Autowired
    AuditionIgnoranceRepository auditionIgnoranceRepository;

    @Autowired
    UserDetails userDetails;

    // @Autowired
    // KafkaProducer kafkaProducer;

    @Override
    public ResponseEntity<?> saveAudition(AuditionWebModel auditionWebModel) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            logger.info("Save audition method start");

            Optional<User> userFromDB = userService.getUser(auditionWebModel.getAuditionCreatedBy());
            if (!userFromDB.isPresent()) {
                return ResponseEntity.ok().body(new Response(-1, "User not found", null));
            }

            Audition audition = new Audition();
            audition.setAuditionTitle(auditionWebModel.getAuditionTitle());
            audition.setUser(userFromDB.get());
            audition.setAuditionExperience(auditionWebModel.getAuditionExperience());
            audition.setAuditionCategory(auditionWebModel.getAuditionCategory());
            audition.setAuditionExpireOn(auditionWebModel.getAuditionExpireOn());
            audition.setAuditionPostedBy(userFromDB.get().getFilmHookCode());
            audition.setAuditionCreatedBy(auditionWebModel.getAuditionCreatedBy());
            audition.setAuditionAddress(auditionWebModel.getAuditionAddress());
            audition.setAuditionMessage(auditionWebModel.getAuditionMessage());
            audition.setAuditionLocation(auditionWebModel.getAuditionLocation());
            audition.setAuditionIsactive(true);

            Audition savedAudition = auditionRepository.save(audition);
            List<AuditionRoles> auditionRolesList = new ArrayList<>();

            if (auditionWebModel.getAuditionRoles().length != 0) {
                String[] auditionRolesArray = auditionWebModel.getAuditionRoles();
                for (String role : auditionRolesArray) {
                    AuditionRoles auditionRoles = new AuditionRoles(); // Create a new instance inside the loop
                    auditionRoles.setAuditionRoleDesc(role);
                    auditionRoles.setAudition(savedAudition);
                    auditionRoles.setAuditionRoleCreatedBy(savedAudition.getAuditionCreatedBy());
                    auditionRoles.setAuditionRoleIsactive(true);

                    auditionRolesList.add(auditionRolesRepository.save(auditionRoles));
                }
            }

            auditionWebModel.getFileInputWebModel().setCategory(MediaFileCategory.Audition);
            auditionWebModel.getFileInputWebModel().setCategoryRefId(savedAudition.getAuditionId()); // adding the story table reference in media files table
            List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService.saveMediaFiles(auditionWebModel.getFileInputWebModel(), userFromDB.get());

            response.put("Audition details", savedAudition);
            response.put("Audition roles", auditionRolesList);
            response.put("Media files", fileOutputWebModelList);

        } catch (Exception e) {
            logger.error("Save audition Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(1, "Audition details saved successfully", response));
    }

    @Override
    public ResponseEntity<?> getAuditionByCategory(Integer categoryId) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            logger.info("get audition by category method start");

            Integer userId = userDetails.userInfo().getId();
            // Fetch the list of ignored auditions for the given user
            List<Integer> ignoredAuditionIds = auditionIgnoranceRepository.findIgnoredAuditionIdsByUserId(userId);

            // Fetch the list of auditions by category and exclude the ignored ones
            List<Audition> auditions = auditionRepository.findByAuditionCategory(categoryId).stream()
                    .filter(audition -> !ignoredAuditionIds.contains(audition.getAuditionId()))
                    .collect(Collectors.toList());

            if (!auditions.isEmpty()) {
                List<AuditionWebModel> auditionWebModelsList = new ArrayList<>();

                for (Audition audition : auditions) {
                    AuditionWebModel auditionWebModel = new AuditionWebModel();

                    auditionWebModel.setAuditionId(audition.getAuditionId());
                    auditionWebModel.setAuditionTitle(audition.getAuditionTitle());
                    auditionWebModel.setAuditionExperience(audition.getAuditionExperience());
                    auditionWebModel.setAuditionCategory(audition.getAuditionCategory());
                    auditionWebModel.setAuditionExpireOn(audition.getAuditionExpireOn());
                    auditionWebModel.setAuditionPostedBy(audition.getAuditionPostedBy());
                    auditionWebModel.setAuditionAddress(audition.getAuditionAddress());
                    auditionWebModel.setAuditionMessage(audition.getAuditionMessage());
                    auditionWebModel.setAuditionLocation(audition.getAuditionLocation());
                    auditionWebModel.setAuditionAttendedCount(acceptanceRepository.getAttendedCount(audition.getAuditionId()));
                    auditionWebModel.setAuditionIgnoredCount(acceptanceRepository.getIgnoredCount(audition.getAuditionId()));

                    if (!audition.getAuditionRoles().isEmpty()) {
                        List<AuditionRolesWebModel> auditionRolesWebModelsList = new ArrayList<>();
                        for (AuditionRoles auditionRoles : audition.getAuditionRoles()) {
                            AuditionRolesWebModel auditionRolesWebModel = new AuditionRolesWebModel();
                            auditionRolesWebModel.setAuditionRoleId(auditionRoles.getAuditionRoleId());
                            auditionRolesWebModel.setAuditionRoleDesc(auditionRoles.getAuditionRoleDesc());
                            auditionRolesWebModelsList.add(auditionRolesWebModel);
                        }
                        auditionWebModel.setAuditionRolesWebModels(auditionRolesWebModelsList);
                    }

                    List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Audition, audition.getAuditionId());
                    if (!Utility.isNullOrEmptyList(fileOutputWebModelList)) {
                        auditionWebModel.setFileOutputWebModel(fileOutputWebModelList);
                    }
                    auditionWebModelsList.add(auditionWebModel);
                }
                response.put("Audition List", auditionWebModelsList);
            } else {
                response.put("No auditions found", "");
            }
        } catch (Exception e) {
            logger.error("get audition by category Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(1, "Audition details fetched successfully", response));
    }

    @Override
    public ResponseEntity<?> auditionAcceptance(AuditionAcceptanceWebModel acceptanceWebModel) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            logger.info("Save audition acceptance method start");
            Optional<Audition> audition = auditionRepository.findById(acceptanceWebModel.getAuditionRefId());
            if (audition.isPresent()) {
                AuditionAcceptanceDetails acceptanceDetails = new AuditionAcceptanceDetails();
                acceptanceDetails.setAuditionAccepted(acceptanceWebModel.isAuditionAccepted());
                acceptanceDetails.setAuditionAcceptanceUser(acceptanceWebModel.getAuditionAcceptanceUser());
                acceptanceDetails.setAuditionRefId(acceptanceWebModel.getAuditionRefId());
                acceptanceDetails.setAuditionAcceptanceCreatedBy(acceptanceWebModel.getAuditionAcceptanceUser());
                acceptanceDetails = acceptanceRepository.save(acceptanceDetails);
                response.put("Audition acceptance", acceptanceDetails);
            }
        } catch (Exception e) {
            logger.error("Save audition acceptance Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(1, "Audition acceptance details saved successfully", response));
    }

    @Override
    public ResponseEntity<?> auditionIgnorance(AuditionIgnoranceWebModel auditionIgnoranceWebModel) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            logger.info("Save audition ignorance method start");

            Optional<Audition> audition = auditionRepository.findById(auditionIgnoranceWebModel.getAuditionRefId());

            if (audition.isPresent()) {
                Optional<AuditionIgnoranceDetails> existingDetailsOptional = auditionIgnoranceRepository.findByAuditionRefIdAndAuditionIgnoranceUser(auditionIgnoranceWebModel.getAuditionRefId(), auditionIgnoranceWebModel.getAuditionIgnoranceUser());

                AuditionIgnoranceDetails ignoranceDetails;
                if (existingDetailsOptional.isPresent()) {
                    // Update the existing record
                    ignoranceDetails = existingDetailsOptional.get();
                    ignoranceDetails.setIgnoranceAccepted(auditionIgnoranceWebModel.isIgnoranceAccepted());
                    ignoranceDetails.setAuditionIgnoranceUpdatedBy(auditionIgnoranceWebModel.getAuditionIgnoranceUser());
                } else {
                    // Create a new record
                    ignoranceDetails = new AuditionIgnoranceDetails();
                    ignoranceDetails.setIgnoranceAccepted(auditionIgnoranceWebModel.isIgnoranceAccepted());
                    ignoranceDetails.setAuditionIgnoranceUser(auditionIgnoranceWebModel.getAuditionIgnoranceUser());
                    ignoranceDetails.setAuditionRefId(auditionIgnoranceWebModel.getAuditionRefId());
                    ignoranceDetails.setAuditionIgnoranceCreatedBy(auditionIgnoranceWebModel.getAuditionIgnoranceUser());
                }

                ignoranceDetails = auditionIgnoranceRepository.save(ignoranceDetails);
                response.put("Audition ignorance details", ignoranceDetails);
            } else {
                return ResponseEntity.ok().body(new Response(-1, "Audition not found", null));
            }
        } catch (Exception e) {
            logger.error("Save audition ignorance method exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(1, "Audition ignorance details saved successfully", response));
    }

    public ResponseEntity<?> getAuditionDetails(AuditionDetailsWebModel auditionDetailsWebModel) {
        // Fetch all AuditionDetails
        List<AuditionDetails> auditionDetailsList = auditionDetailsRepository.findAll();

        if (auditionDetailsList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Create a list to hold the response data
        List<Map<String, Object>> responseList = auditionDetailsList.stream().map(auditionDetails -> {
            Map<String, Object> response = new HashMap<>();
            response.put("auditionDetailsId", auditionDetails.getAuditionDetailsId());
            response.put("auditionDetailsName", auditionDetails.getAuditionDetailsName());
            return response;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    @Override
    public ResponseEntity<?> getAllAddressList() {
        List<AddressList> addressLists = addressListRepository.findAll().parallelStream()
                .filter(address -> address.getStatus().equals(true) && !Utility.isNullOrBlankWithTrim(address.getAuditionAddress()))
                .collect(Collectors.toList());
        List<AddressListWebModel> result = addressLists.stream()
                .map(addr -> AddressListWebModel.builder()
                        .id(addr.getId())
                        .address(addr.getAuditionAddress())
                        .status(addr.getStatus())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<?> getAddressList(String address) {
        List<AddressList> addressLists = addressListRepository.findByAuditionAddressContainingIgnoreCase(address).parallelStream()
                .filter(addressList -> addressList.getStatus().equals(true))
                .collect(Collectors.toList());
        List<AddressListWebModel> result = addressLists.stream()
                .map(addr -> AddressListWebModel.builder()
                        .id(addr.getId())
                        .address(addr.getAuditionAddress())
                        .status(addr.getStatus())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<?> getAuditionByFilterAddress(Integer categoryId, String searchKey) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            logger.info("get audition by category and address method start");

            Integer userId = userDetails.userInfo().getId();
            // Fetch the list of ignored auditions for the given user
            List<Integer> ignoredAuditionIds = auditionIgnoranceRepository.findIgnoredAuditionIdsByUserId(userId);

            // Fetch the list of auditions by category
            List<Audition> auditionsByCategory = auditionRepository.findByAuditionCategory(categoryId).stream()
                    .filter(audition -> !ignoredAuditionIds.contains(audition.getAuditionId()))
                    .collect(Collectors.toList());

            // Filter auditions by search key
            List<Audition> auditionsWithSearchKey = new ArrayList<>();
            List<Audition> auditionsWithoutSearchKey = new ArrayList<>();
            for (Audition audition : auditionsByCategory) {
                if (auditionContainsSearchKey(audition, searchKey)) {
                    auditionsWithSearchKey.add(audition);
                } else {
                    auditionsWithoutSearchKey.add(audition);
                }
            }

            // Combine auditions with search key and without
            List<Audition> combinedAuditions = new ArrayList<>();
            combinedAuditions.addAll(auditionsWithSearchKey);
            combinedAuditions.addAll(auditionsWithoutSearchKey);

            if (!combinedAuditions.isEmpty()) {
                List<AuditionWebModel> auditionWebModelsList = new ArrayList<>();

                for (Audition audition : combinedAuditions) {
                    AuditionWebModel auditionWebModel = new AuditionWebModel();

                    auditionWebModel.setAuditionId(audition.getAuditionId());
                    auditionWebModel.setAuditionTitle(audition.getAuditionTitle());
                    auditionWebModel.setAuditionExperience(audition.getAuditionExperience());
                    auditionWebModel.setAuditionCategory(audition.getAuditionCategory());
                    auditionWebModel.setAuditionExpireOn(audition.getAuditionExpireOn());
                    auditionWebModel.setAuditionPostedBy(audition.getAuditionPostedBy());
                    auditionWebModel.setAuditionAddress(audition.getAuditionAddress());
                    auditionWebModel.setAuditionLocation(audition.getAuditionLocation());
                    auditionWebModel.setAuditionMessage(audition.getAuditionMessage());
                    auditionWebModel.setAuditionAttendedCount(acceptanceRepository.getAttendedCount(audition.getAuditionId()));
                    auditionWebModel.setAuditionIgnoredCount(acceptanceRepository.getIgnoredCount(audition.getAuditionId()));

                    if (!audition.getAuditionRoles().isEmpty()) {
                        List<AuditionRolesWebModel> auditionRolesWebModelsList = new ArrayList<>();
                        for (AuditionRoles auditionRoles : audition.getAuditionRoles()) {
                            AuditionRolesWebModel auditionRolesWebModel = new AuditionRolesWebModel();
                            auditionRolesWebModel.setAuditionRoleId(auditionRoles.getAuditionRoleId());
                            auditionRolesWebModel.setAuditionRoleDesc(auditionRoles.getAuditionRoleDesc());

                            auditionRolesWebModelsList.add(auditionRolesWebModel);
                        }
                        auditionWebModel.setAuditionRolesWebModels(auditionRolesWebModelsList);
                    }

                    List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService.getMediaFilesByCategoryAndRefId(MediaFileCategory.Audition, audition.getAuditionId());
                    if (!Utility.isNullOrEmptyList(fileOutputWebModelList)) {
                        auditionWebModel.setFileOutputWebModel(fileOutputWebModelList);
                    }

                    auditionWebModelsList.add(auditionWebModel);
                }
                response.put("Audition List", auditionWebModelsList);
            } else {
                response.put("No auditions found", "");
            }

        } catch (Exception e) {
            logger.error("get audition by category and address Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(1, "Audition details fetched successfully", response));
    }

    private boolean auditionContainsSearchKey(Audition audition, String searchKey) {
        // Implement your logic to check if the search key is present in the audition
        // Example: Check if the audition address contains the search key
        return audition.getAuditionAddress().toLowerCase().contains(searchKey.toLowerCase());
    }


    @Transactional
    public ResponseEntity<?> deleteAuditionById(Integer auditionId, Integer userId) {
        try {
            Optional<Audition> auditionData = auditionRepository.findById(auditionId);
            if (auditionData.isPresent()) {
                Audition audition = auditionData.get();
                logger.info("User ID: {}", userId);
                logger.info("Audition Created By: {}", audition.getAuditionCreatedBy());
                if (audition.getAuditionCreatedBy().equals(userId)) {
                    auditionRolesRepository.deleteByAuditionId(auditionId);
                    auditionRepository.deleteById(auditionId);
                    //auditionRolesRepository.deleteByAuditionId(auditionId);
                    acceptanceRepository.deleteByAuditionRefId(auditionId); // Delete related AuditionAcceptanceDetails
                    auditionIgnoranceRepository.deleteByAuditionRefId(auditionId);
                    return ResponseEntity.ok(new Response(1, "Audition deleted successfully.", null));
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response(-1, "Unauthorized: You do not have permission to delete this audition.", null));
                }
            } else {
                return ResponseEntity.ok().body(new Response(-1, "Audition not found.", null));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> updateAudition(AuditionWebModel auditionWebModel) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            logger.info("Update audition method start");

            // Check if the auditionId is provided
            if (auditionWebModel.getAuditionId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(-1, "Audition ID is required for updating.", null));
            }

            // Check if the audition exists
            Optional<Audition> existingAuditionOptional = auditionRepository.findById(auditionWebModel.getAuditionId());
            if (existingAuditionOptional.isEmpty()) {
                return ResponseEntity.ok().body(new Response(-1, "Audition not found.", null));
            }

            Audition existingAudition = existingAuditionOptional.get();

            // Update audition details
            existingAudition.setAuditionTitle(auditionWebModel.getAuditionTitle());
            existingAudition.setAuditionExperience(auditionWebModel.getAuditionExperience());
            existingAudition.setAuditionCategory(auditionWebModel.getAuditionCategory());
            existingAudition.setAuditionExpireOn(auditionWebModel.getAuditionExpireOn());
            existingAudition.setAuditionAddress(auditionWebModel.getAuditionAddress());
            existingAudition.setAuditionMessage(auditionWebModel.getAuditionMessage());
            existingAudition.setAuditionLocation(auditionWebModel.getAuditionLocation());

            // Update the audition
            Audition savedAudition = auditionRepository.save(existingAudition);


            auditionWebModel.getFileInputWebModel().setCategory(MediaFileCategory.Audition);
            auditionWebModel.getFileInputWebModel().setCategoryRefId(savedAudition.getAuditionId()); // adding the story
            List<FileOutputWebModel> fileOutputWebModelList = mediaFilesService.saveMediaFiles(auditionWebModel.getFileInputWebModel(), existingAudition.getUser());

            mediaFilesService.deleteMediaFilesByUserIdAndCategoryAndRefIds(savedAudition.getUser().getUserId(), MediaFileCategory.Audition, auditionWebModel.getMediaFilesIds());


            List<AuditionRoles> auditionRolesList = new ArrayList<>();

            // Update existing roles if auditionRolesId is provided
            if (!Utility.isNullOrEmptyList(auditionWebModel.getAuditionRolesWebModels())) {
                for (AuditionRolesWebModel role : auditionWebModel.getAuditionRolesWebModels()) {
                    if (role.getAuditionRoleId() != null) {
                        Optional<AuditionRoles> existingRoleOptional = auditionRolesRepository.findById(role.getAuditionRoleId());
                        if (existingRoleOptional.isPresent()) {
                            AuditionRoles existingRole = existingRoleOptional.get();
                            existingRole.setAuditionRoleDesc(role.getAuditionRoleDesc());
                            auditionRolesList.add(auditionRolesRepository.save(existingRole));
                        }
                    }
                }
            }

            // Create new roles if auditionRolesId is not provided
            if (auditionWebModel.getAuditionRoles() != null) {
                for (String roleDesc : auditionWebModel.getAuditionRoles()) {
                    AuditionRoles newRole = new AuditionRoles();
                    newRole.setAudition(savedAudition);
                    newRole.setAuditionRoleDesc(roleDesc);
                    newRole.setAuditionRoleCreatedBy(auditionWebModel.getAuditionCreatedBy());
                    newRole.setAuditionRoleIsactive(true);
                    auditionRolesList.add(auditionRolesRepository.save(newRole));
                }
            }

            response.put("Audition details", savedAudition);
        } catch (Exception e) {
            logger.error("Update audition Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(1, "Audition details updated successfully", response));
    }

}
