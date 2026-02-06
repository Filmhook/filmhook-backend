package com.annular.filmhook.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.PromoteAd;
import com.annular.filmhook.service.PromoteAdService;
import com.annular.filmhook.webmodel.PromoteWebModel;
import com.annular.filmhook.webmodel.VisitPageWebModel;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/promote")
@RequiredArgsConstructor
public class PromoteAdController {

    private final PromoteAdService promoteAdService;
    @Autowired
	private  UserDetails userDetails;
      
    @PostMapping("/save")
    public ResponseEntity<Response> savePromote(
            @ModelAttribute PromoteWebModel model,
            @RequestParam Integer userId) {

        return ResponseEntity.ok(promoteAdService.savePromote(model, userId));
       
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getPromoteData(@PathVariable Integer postId) { 
        PromoteAd result = promoteAdService.getPromoteByPostId(postId);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/payment/success")
    public ResponseEntity<Response> paymentSuccess(@RequestBody PromoteWebModel model) {
        return ResponseEntity.ok(promoteAdService.updatePaymentSuccess(model));
    }

    @PostMapping("/payment/failure")
    public ResponseEntity<Response> paymentFailed(@RequestBody PromoteWebModel model) {
        return ResponseEntity.ok(promoteAdService.updatePaymentFailed(model));
    }
    
   
    // ⭐ GET RECENT PROMOTED (Running + Completed only)
    
    @GetMapping("/recent")
    public ResponseEntity<Response> getRecentPromoted() {

        Integer userId = userDetails.userInfo().getId();

        return ResponseEntity.ok(promoteAdService.getRecentPromotions(userId));
    }
   
    
    @GetMapping("/getObjectiveTypes")
    public ResponseEntity<List<VisitPageWebModel>> getAllCategories() {
        return ResponseEntity.ok(promoteAdService.getAllObjectives());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<VisitPageWebModel>> getPagesByCategory(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(promoteAdService.getPagesByCategoryId(categoryId));
    }

    @GetMapping("/page/{visitPageId}/details")
    public ResponseEntity<List<VisitPageWebModel>> getVisitPageDetails(
            @PathVariable Integer visitPageId) {
        return ResponseEntity.ok(promoteAdService.getDetailsByVisitPageId(visitPageId));
    }


}
