package com.annular.filmhook.controller;

import com.annular.filmhook.model.SellerInfo;
import com.annular.filmhook.service.SellerService;
import com.annular.filmhook.webmodel.SellerFileInputModel;
import com.annular.filmhook.webmodel.SellerInfoDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

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

    @DeleteMapping("/delete/{sellerId}")
    public ResponseEntity<?> deleteSeller(@PathVariable Long sellerId) {
        try {
            sellerService.deleteSellerById(sellerId);
            return ResponseEntity.ok("Seller with ID " + sellerId + " deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
