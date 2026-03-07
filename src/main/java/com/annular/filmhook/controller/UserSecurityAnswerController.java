package com.annular.filmhook.controller;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.service.UserSecurityAnswerService;
import com.annular.filmhook.webmodel.UserSecurityAnswerDTO;
import com.annular.filmhook.webmodel.UserWebModel;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/security")
public class UserSecurityAnswerController {

    @Autowired
    private UserSecurityAnswerService service;

    @Autowired
    private UserDetails userDetails;
    
    @PostMapping("/saveSecurity")
    public ResponseEntity<List<UserSecurityAnswerDTO>> saveSecurityQuestions(@RequestBody List<UserSecurityAnswerDTO> dtoList) {
        Integer loggedInUserId = userDetails.userInfo().getId();
        List<UserSecurityAnswerDTO> response = service.saveSecurityQuestions(dtoList, loggedInUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/securityQuestions")
    public ResponseEntity<Response> getAllSecurityQuestions() {
        Response response = service.getAllSecurityQuestions();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/myQuestionsWithAnswers")
    public ResponseEntity<Response> getQuestionsWithAnswers() {

        Integer userId = userDetails.userInfo().getId();

        return ResponseEntity.ok(
                service.getUserSecurityQuestionsWithAnswers(userId)
        );
    }
    
    
    @PostMapping("/sendSeurityEmailOtp")
    public ResponseEntity<Response> sendSecurityOtp() {

        Integer userId = userDetails.userInfo().getId();

        return ResponseEntity.ok(
                service.sendSecurityEditOtp(userId)
        );
    }

    // ✅ 2️⃣ Verify OTP
    @PostMapping("/verifySeurityEmailOtp")
    public ResponseEntity<Response> verifySecurityOtp( @RequestParam String securityOtp) {

        Integer userId = userDetails.userInfo().getId();

        return ResponseEntity.ok(
                service.verifySecurityEditOtp(userId, securityOtp)
        );
    }
    
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(
            @RequestBody UserWebModel userWebModel) {

        return service.changingPassword(userWebModel);
    }

    @PostMapping("/verifySecurityQuestion/{userId}")
    public ResponseEntity<Response> verifySecurityAnswers(@PathVariable Integer userId,
            @RequestBody List<UserSecurityAnswerDTO> requestList) {
        return ResponseEntity.ok(
                service.verifySecurityAnswers(userId, requestList)
        );
    }
    
    @GetMapping("/myQuestions/{userId}")
    public ResponseEntity<Response> getQuestions(@PathVariable Integer userId) {

        return ResponseEntity.ok(
                service.getUserSecurityQuestionsWithAnswers(userId)
        );
    }
}
