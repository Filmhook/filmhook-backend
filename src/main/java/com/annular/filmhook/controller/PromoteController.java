package com.annular.filmhook.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.VisitPage;
import com.annular.filmhook.model.VisitePageCategory;
import com.annular.filmhook.repository.VisitPageRepository;
import com.annular.filmhook.service.PostService;
import com.annular.filmhook.service.PromoteService;
import com.annular.filmhook.webmodel.PostWebModel;
import com.annular.filmhook.webmodel.PromoteWebModel;

@RestController
@RequestMapping("/promote")
public class PromoteController {

    public static final Logger logger = LoggerFactory.getLogger(PromoteController.class);

    @Autowired
    PromoteService promoteService;

    
    @Autowired
    PostService postService;
    
    @PostMapping("/addPromote")
    public ResponseEntity<?> addPromote(@RequestBody PromoteWebModel promoteWebModel) {
        try {
            logger.info("addPromote controller start");
            return promoteService.addPromote(promoteWebModel);
        } catch (Exception e) {
            logger.error("addPromote  Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }
    
    @RequestMapping(value = "/addPromoteApi", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> addPromotes(@ModelAttribute PromoteWebModel promoteWebModel) {
        try {
            logger.info("addPromotes controller start");
            
            // Assuming promoteService.addPromote() handles the logic of saving the promotion.
            ResponseEntity<HashMap<String, Object>> response = promoteService.addPromotes(promoteWebModel);
            
            // Handle the response from the service as needed.
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("addPromote Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }


    @PostMapping("/updatePromote")
    public ResponseEntity<?> updatePromote(@RequestBody PromoteWebModel promoteWebModel) {
        try {
            logger.info("updatePromote controller start");
            return promoteService.updatePromote(promoteWebModel);
        } catch (Exception e) {
            logger.error("updatePromote  Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }

    @PostMapping("/deletePromote")
    public ResponseEntity<?> deletePromote(@RequestBody PromoteWebModel promoteWebModel) {
        try {
            logger.info("deletePromote controller start");
            return promoteService.deletePromote(promoteWebModel);
        } catch (Exception e) {
            logger.error("deletePromote  Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }

    @PostMapping("/getAllPromote")
    public ResponseEntity<?> getAllPromote(@RequestBody PromoteWebModel promoteWebModel) {
        try {
            logger.info("getAllPromote controller start");
            return promoteService.getAllPromote(promoteWebModel);
        } catch (Exception e) {
            logger.error("getAllPromote  Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }

    @PostMapping("/getByPromoteId")
    public ResponseEntity<?> getByPromoteId(@RequestBody PromoteWebModel promoteWebModel) {
        try {
            logger.info("getByPromoteId controller start");
            return promoteService.getByPromoteId(promoteWebModel);
        } catch (Exception e) {
            logger.error("getByPromoteId  Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }

    @GetMapping("/getPromoteByUserId")
    public Response getPromoteByUserId(@RequestParam("userId") Integer userId) {
        try {
            List<PostWebModel> outputList = postService.getPostsByUserIds(userId);
            if (outputList != null && !outputList.isEmpty()) {
                return new Response(1, "Post(s) found successfully...", outputList);
            } else {
                return new Response(-1, "No file(s) available for this user...", null);
            }
        } catch (Exception e) {
            logger.error("Error at getPostsByUserId() -> {}", e.getMessage());
        }
        return new Response(-1, "Post files were not found...", null);
    }

    @PostMapping("/deletePromoteByUserId")
    public ResponseEntity<?> deletePromoteByUserId(@RequestBody PromoteWebModel promoteWebModel) {
        try {
            logger.info("deletePromoteByUserId controller start");
            return promoteService.deletePromoteByUserId(promoteWebModel);
        } catch (Exception e) {
            logger.error("deletePromoteByUserId  Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }
    
    @PostMapping("/addVisitPage")
    public ResponseEntity<?> addVisitPage(@RequestBody PromoteWebModel promoteWebModel) {
        try {
            logger.info("addVisitPage controller start");
            return promoteService.addVisitPage(promoteWebModel);
        } catch (Exception e) {
            logger.error("addVisitPage  Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }
    
    @GetMapping("/getVisitType")
    public ResponseEntity<?> getVisitType() {
        try {
            logger.info("getVisitType controller start");
            return promoteService.getVisitType();
        } catch (Exception e) {
            logger.error("getVisitType  Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }

    @PostMapping("/selectPromoteOption")
    public ResponseEntity<?> selectPromoteOption(@RequestBody PromoteWebModel promoteWebModel) {
        try {
            logger.info("selectPromoteOption controller start");
            return promoteService.selectPromoteOption(promoteWebModel);
        } catch (Exception e) {
            logger.error("updatePromote  Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }
    
    @PostMapping("/getDescriptionByPostId")
    public ResponseEntity<?> getDescriptionByPostId(@RequestBody PostWebModel postWebModel) {
        try {
            logger.info("getDescriptionByPostId controller start");
            return promoteService.getDescriptionByPostId(postWebModel);
        } catch (Exception e) {
            logger.error("updatePromotepostWebModel  Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }
    @PostMapping("/updateDescriptionByPostId")
    public ResponseEntity<?> updateDescriptionByPostId(@RequestBody PostWebModel postWebModel) {
        try {
            logger.info("updateDescriptionByPostId controller start");
            return promoteService.updateDescriptionByPostId(postWebModel);
        } catch (Exception e) {
            logger.error("updateDescriptionByPostId  Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }
    @GetMapping("/getVisitTypeByWhatsApp")
    public ResponseEntity<?> getVisitTypeByWhatsApp() {
        try {
            logger.info("getVisitTypeByWhatsApp controller start");
            return promoteService.getVisitTypeByWhatsApp();
        } catch (Exception e) {
            logger.error("getVisitTypeByWhatsApp  Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }
    
    @PostMapping("/updatePromoteStatus")
    public ResponseEntity<?> updatePromoteStatus(@RequestBody PromoteWebModel promoteWebModel) {
        try {
            logger.info("updatePromoteStatus controller start");
            return promoteService.updatePromoteStatus(promoteWebModel);
        } catch (Exception e) {
            logger.error("updatePromoteStatus  Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Response(-1, "Fail", ""));
        }
    }
    
    @PostMapping("/add")
    public ResponseEntity<VisitPage> addVisitPage(@RequestBody VisitPage visitPage) {
        VisitPage savedVisitPage = promoteService.addVisitPage(visitPage);
        return ResponseEntity.ok(savedVisitPage);
    }
    
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<VisitPage>> getPagesByCategory(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(promoteService.getPagesByCategoryId(categoryId));
    }

    
    
    @GetMapping("/getAllCategory")
    public ResponseEntity<List<VisitePageCategory>> getAllCategories() {
        return ResponseEntity.ok(promoteService.getAllCategories());
    }
}

