package com.annular.filmhook.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
import com.annular.filmhook.service.DetailService;
import com.annular.filmhook.webmodel.DetailRequest;
import com.annular.filmhook.webmodel.FileOutputWebModel;
import com.annular.filmhook.webmodel.IndustryFileInputWebModel;
import com.annular.filmhook.webmodel.IndustryTemporaryWebModel;
import com.annular.filmhook.webmodel.IndustryUserPermanentDetailWebModel;

@RestController
@RequestMapping("/industryUser")
public class DetailsController {

	public static final Logger logger = LoggerFactory.getLogger(DetailsController.class);

	@Autowired
	DetailService detailService;

	@PostMapping("/getDetails")
	public ResponseEntity<?> getDetails(@RequestBody DetailRequest detailRequest) {
		try {
			logger.info("getDetails controller start");
			return detailService.getDetails(detailRequest);
		} catch (Exception e) {
			logger.error("getDetails Method Exception: {}", e);
			e.printStackTrace();
			return ResponseEntity.ok(new Response(-1, "Fail", ""));
		}
	}

	@PostMapping("/addTemporaryDetails")
	public ResponseEntity<?> addTemporaryDetails(@RequestBody IndustryTemporaryWebModel industryTemporaryWebModel) {
		try {
			logger.info("addTemporaryDetails controller start");
			return detailService.addTemporaryDetails(industryTemporaryWebModel);
		} catch (Exception e) {
			logger.error("industryTemporaryWebModel Method Exception: {}", e);
			e.printStackTrace();
			return ResponseEntity.ok(new Response(-1, "Fail", ""));
		}
	}

	@PostMapping("/updateTemporaryDetails")
	public ResponseEntity<?> updateTemporaryDetails(@RequestBody IndustryTemporaryWebModel industryTemporaryWebModel) {
		try {
			logger.info("updateTemporaryDetails controller start");
			return detailService.updateTemporaryDetails(industryTemporaryWebModel);
		} catch (Exception e) {
			logger.error("updateTemporaryDetails Method Exception: {}", e);
			e.printStackTrace();
			return ResponseEntity.ok(new Response(-1, "Fail", ""));
		}
	}

	@PostMapping("/getTemporaryDetails")
	public ResponseEntity<?> getTemporaryDetails(@RequestBody IndustryTemporaryWebModel industryTemporaryWebModel) {
		try {
			logger.info("getTemporaryDetails controller start");
			return detailService.getTemporaryDetails(industryTemporaryWebModel);
		} catch (Exception e) {
			logger.error("getTemporaryWebModel Method Exception: {}", e);
			e.printStackTrace();
			return ResponseEntity.ok(new Response(-1, "Fail", ""));
		}
	}

	@PostMapping("/getTemporaryDuplicateDetails")
	public ResponseEntity<?> getTemporaryDuplicateDetails(
			@RequestBody IndustryTemporaryWebModel industryTemporaryWebModel) {
		try {
			logger.info("getTemporaryDuplicateDetails controller start");
			return detailService.getTemporaryDuplicateDetails(industryTemporaryWebModel);
		} catch (Exception e) {
			logger.error("getTemporaryDuplicateDetails Method Exception: {}", e);
			e.printStackTrace();
			return ResponseEntity.ok(new Response(-1, "Fail", ""));
		}
	}

	@PostMapping("/addIndustryUserPermanentDetails")
	public ResponseEntity<?> addIndustryUserPermanentDetails(@RequestParam Integer userId,
			@RequestBody List<IndustryUserPermanentDetailWebModel> industryUserPermanentDetailWebModels) {
		try {
			logger.info("addIndustryUserPermanentDetails controller start");
			return detailService.addIndustryUserPermanentDetails(userId, industryUserPermanentDetailWebModels);
		} catch (Exception e) {
			logger.error("addIndustryUserPermanentDetails Method Exception: {}", e);
			e.printStackTrace();
			return ResponseEntity.ok(new Response(-1, "Fail", ""));
		}
	}

	@PostMapping("/getIndustryUserPermanentDetails")
	public ResponseEntity<?> getIndustryUserPermanentDetails(@RequestParam Integer userId) {
		try {
			logger.info("getIndustryUserPermanentDetails controller start");
			return detailService.getIndustryUserPermanentDetails(userId);
		} catch (Exception e) {
			logger.error("getIndustryUserPermanentDetails Method Exception: {}", e);
			e.printStackTrace();
			return ResponseEntity.ok(new Response(-1, "Fail", ""));
		}
	}
	@RequestMapping(path = "/saveIndustryUserFiles", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Response saveIndustryUserFiles(@ModelAttribute IndustryFileInputWebModel inputFileData) {
		try {
			FileOutputWebModel outputFileData = detailService.saveIndustryUserFiles(inputFileData);
			if (outputFileData != null)
				return new Response(1, "File(s) saved successfully...", outputFileData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Response(-1, "Error occurred while saving files...", null);
	}

	@GetMapping("/getIndustryFilesByUserId")
	public Response getIndustryFiles(@RequestParam("userId") Integer userId) {
		try {
			List<FileOutputWebModel> outputList = detailService.getIndustryFiles(userId);
			if (outputList != null && !outputList.isEmpty()) {
				logger.info("[" + outputList.size() + "] Industry files found for userId :- " + userId);
				return new Response(1, "Gallery file(s) found successfully...", outputList);
			} else {
				return new Response(-1, "No file(s) available for this user...", null);
			}
		} catch (Exception e) {
			logger.error("Error at getGalleryFiles()...", e);
		}
		return new Response(-1, "Files were not found...", null);
	}
	
    @GetMapping("/downloadIndustryFile")
    public ResponseEntity<?> downloadGalleryFile(@RequestParam("userId") Integer userId,
                                                 @RequestParam("category") String category,
                                                 @RequestParam("fileId") String fileId) {
        try {
            logger.info("downloadIndustryFile Input Category :- " + category + ", File Id :- " + fileId);
            Resource resource = detailService.getIndustryFile(userId, category, fileId);
            if (resource != null) {
                String contentType = "application/octet-stream";
                String headerValue = "attachment; filename=\"" + fileId + "\"";
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                        .body(resource);
            }
        } catch (Exception e) {
            logger.error("Error at downloadIndustryFile()...", e);
        }
        return ResponseEntity.internalServerError().build();
    }
    
    @PostMapping("/updateIndustryUserPermanentDetails")
	public ResponseEntity<?> updateIndustryUserPermanentDetails(@RequestBody List<IndustryUserPermanentDetailWebModel> industryUserPermanentDetailWebModels) {
		try {
			logger.info("updateIndustryUserPermanentDetails controller start");
			return detailService.updateIndustryUserPermanentDetails(industryUserPermanentDetailWebModels);
		} catch (Exception e) {
			logger.error("updateIndustryUserPermanentDetails Method Exception: {}", e);
			e.printStackTrace();
			return ResponseEntity.ok(new Response(-1, "Fail", ""));
		}
	}
}