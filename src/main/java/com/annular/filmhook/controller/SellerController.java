package com.annular.filmhook.controller;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.SellerInfo;
import com.annular.filmhook.repository.SellerInfoRepository;
import com.annular.filmhook.service.SellerService;
import com.annular.filmhook.webmodel.SellerFileInputModel;
import com.annular.filmhook.webmodel.SellerInfoDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;
   private final SellerInfoRepository sellerInfoRepository;
    
	   @GetMapping("/check-seller/{userId}")
	   public ResponseEntity<Map<String, Object>> checkIfUserIsSeller(@PathVariable Integer userId) {
	       Map<String, Object> response = new HashMap<>();
	       try {
	           Optional<SellerInfo> sellerOptional = sellerInfoRepository.findSellerInfoByUserId(userId);
	
	           if (sellerOptional.isPresent()) {
	               response.put("status", true);
	               response.put("message", "Seller account found for user ID: " + userId);
	               response.put("sellerId", sellerOptional.get().getId());
	           } else {
	               response.put("status", false);
	               response.put("message", "No seller account associated with user ID: " + userId);
	           }
	
	           return ResponseEntity.ok(response);
	       } catch (Exception e) {
	           response.put("status", false);
	           response.put("message", "Error checking seller account: " + e.getMessage());
	           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	       }
	   }

    @PostMapping("/save")
    public ResponseEntity<?> saveSellerInfo(
            @RequestPart("seller") SellerInfoDTO sellerInfoDTO,
            @RequestPart(value = "idProofImages", required = false) List<MultipartFile> idProofImages,
            @RequestPart(value = "shopLogos", required = false) List<MultipartFile> shopLogos) {
        try {
            SellerFileInputModel fileInput = new SellerFileInputModel();
            fileInput.setIdProofImages(idProofImages);
            fileInput.setShopLogos(shopLogos);

            SellerInfo savedSeller = sellerService.saveSellerInfo(sellerInfoDTO, fileInput);
            return ResponseEntity.ok(savedSeller);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to save seller info: " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSellerInfo(
            @PathVariable Long id,
            @RequestPart("seller") SellerInfoDTO sellerInfoDTO,
            @RequestPart(value = "idProofImages", required = false) List<MultipartFile> idProofImages,
            @RequestPart(value = "shopLogos", required = false) List<MultipartFile> shopLogos) {
        try {
            SellerFileInputModel fileInput = new SellerFileInputModel();
            fileInput.setIdProofImages(idProofImages);
            fileInput.setShopLogos(shopLogos);

            SellerInfo updatedSeller = sellerService.updateSellerInfo(id, sellerInfoDTO, fileInput);
            return ResponseEntity.ok(updatedSeller);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to update seller info: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getSellerDetailsByUserId(@PathVariable Integer userId) {
        try {
            SellerInfoDTO response = sellerService.getSellerDetailsByUserId(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Seller not found for user ID: " + userId);
        }
    }
        
    @DeleteMapping("/deleteSeller/{sellerId}")
    public ResponseEntity<Response> deleteSeller(@PathVariable Long sellerId) {
        try {
            sellerService.deleteSellerById(sellerId);
            return ResponseEntity.ok(new Response(1, "Seller and all associated data deleted successfully", null));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatus())
                    .body(new Response(-1, e.getReason(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(-1, "Unexpected error occurred while deleting seller", e.getMessage()));
        }
    }

}
