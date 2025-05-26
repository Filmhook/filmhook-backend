package com.annular.filmhook.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;

import com.annular.filmhook.service.AuditionService;

import com.annular.filmhook.webmodel.AuditionAcceptanceWebModel;
import com.annular.filmhook.webmodel.AuditionDetailsWebModel;
import com.annular.filmhook.webmodel.AuditionIgnoranceWebModel;
//import com.annular.filmhook.service.impl.KafkaProducer;
import com.annular.filmhook.webmodel.AuditionWebModel;

@RestController
@RequestMapping("/audition")
public class AuditionController {

    public static final Logger logger = LoggerFactory.getLogger(AuditionController.class);

    @Autowired
    AuditionService auditionService;

//	@Autowired
//	KafkaProducer kafkaProducer;

    @RequestMapping(path = "/saveAudition", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> saveAudition(@ModelAttribute AuditionWebModel auditionWebModel) {
        try {
            logger.info("Audition data to be saved :- {}", auditionWebModel);
            return auditionService.saveAudition(auditionWebModel);
        } catch (Exception e) {
            logger.error("Save audition Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(1, "Success", ""));
    }

    @PostMapping("/getAuditionByCategory")
    public ResponseEntity<?> getAuditionByCategory(@RequestBody AuditionWebModel auditionWebModel) {
        try {
            // logger.info("Audition category to be fetched :- " + auditionWebModel.getAuditionCategory());
            if (!auditionWebModel.getFlag()) {
                return auditionService.getAuditionByCategory(auditionWebModel.getAuditionCategory());
            } else if (auditionWebModel.getFlag()) {
                return auditionService.getAuditionByFilterAddress(auditionWebModel.getAuditionCategory(), auditionWebModel.getSearchKey());
            } else {
                return ResponseEntity.badRequest().body(new Response(-1, "Invalid flag value", ""));
            }
        } catch (Exception e) {
            logger.error("getAuditionByCategory Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(1, "Success", ""));
    }

    @PostMapping("/auditionAcceptance")
    public ResponseEntity<?> auditionAcceptance(@RequestBody AuditionAcceptanceWebModel acceptanceWebModel) {
        try {
            logger.info("Audition Acceptance to be saved :- {}", acceptanceWebModel.isAuditionAccepted());
            return auditionService.auditionAcceptance(acceptanceWebModel);
        } catch (Exception e) {
            logger.error("Save Audition Acceptance Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(1, "Success", ""));
    }

    @RequestMapping(path = "/saveAuditions", method = RequestMethod.POST)
    public ResponseEntity<?> saveAuditions() {
        try {
            // kafkaProducer.sendNotification(">>>>>>>>>>>>>>>>>>>>>>My first notification");
            return null;
        } catch (Exception e) {
            logger.error("saveAuditions Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(1, "Success", ""));
    }

    @PostMapping("/auditionIgnorance")
    public ResponseEntity<?> auditionIgnorance(@RequestBody AuditionIgnoranceWebModel auditionIgnoranceWebModel) {
        try {
            return auditionService.auditionIgnorance(auditionIgnoranceWebModel);
        } catch (Exception e) {
            logger.error("auditionIgnorance Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(1, "Success", ""));
    }

    @PostMapping("/getAuditionDetails")
    public ResponseEntity<?> getAuditionDetails(@RequestBody AuditionDetailsWebModel auditionDetailsWebModel) {
        try {
            return auditionService.getAuditionDetails(auditionDetailsWebModel);
        } catch (Exception e) {
            logger.error("getAuditionDetails Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(1, "Success", ""));
    }

    @GetMapping("/getAllAddressList")
    public ResponseEntity<?> getAllAddressList() {
        try {
            return auditionService.getAllAddressList();
        } catch (Exception e) {
            logger.error("getAllAddressList Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(1, "Success", ""));
    }

    @GetMapping("/getAddressList")
    public ResponseEntity<?> getAddressList(@RequestParam("address") String address) {
        try {
            return auditionService.getAddressList(address);
        } catch (Exception e) {
            logger.error("getAddressList Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(1, "Success", ""));
    }


    @PostMapping("/deleteAuditionById")
    public ResponseEntity<?> deleteAuditionById(@RequestBody AuditionWebModel auditionWebModel) {
        try {
            return auditionService.deleteAuditionById(auditionWebModel.getAuditionId(), auditionWebModel.getAuditionCreatedBy());
        } catch (Exception e) {
            logger.error("deleteAuditionById Method Exception -> {}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(new Response(1, "Success", ""));
    }

    @PostMapping("/updateAudition")
    public ResponseEntity<?> updateAudition(@ModelAttribute AuditionWebModel auditionWebModel) {
        try {
            if (auditionWebModel.getAuditionId() == null)
                return ResponseEntity.badRequest().body(new Response(-1, "Audition ID is required for updating.", null));
            return auditionService.updateAudition(auditionWebModel);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
    }
    @PostMapping("/getAcceptanceDetailsByUserId")
    public ResponseEntity<?> getAcceptanceDetailsByUserId(@RequestBody AuditionWebModel auditionWebModel) {
        try {
            if (auditionWebModel.getAuditionRefId() == null)
                return ResponseEntity.badRequest().body(new Response(-1, "getAcceptanceDetailsByUserId is required .", null));
            return auditionService.getAcceptanceDetailsByUserId(auditionWebModel);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
    }
    
    @PostMapping("/getAuditionByUserId")
    public ResponseEntity<?> getAuditionByUserId(@RequestBody AuditionWebModel auditionWebModel) {
        try {
            
            return auditionService.getAuditionByUserId(auditionWebModel);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
    }
    @PostMapping("/getAuditionAcceptanceListByUserId")
    public ResponseEntity<?> getAuditionAcceptanceListByUserId(@RequestBody AuditionWebModel auditionWebModel) {
        try {
            
            return auditionService.getAuditionAcceptanceListByUserId(auditionWebModel);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
    }
}
