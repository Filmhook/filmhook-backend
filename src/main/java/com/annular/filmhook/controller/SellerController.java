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
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @PostMapping("/save")
    public ResponseEntity<SellerInfo> saveSellerInfo(
            @RequestPart("seller") SellerInfoDTO sellerInfoDTO,
            @RequestPart(value = "idProofImages", required = false) List<MultipartFile> idProofImages,
            @RequestPart(value = "shopLogos", required = false) List<MultipartFile> shopLogos) {

        SellerFileInputModel fileInput = new SellerFileInputModel();
        fileInput.setIdProofImages(idProofImages);
        fileInput.setShopLogos(shopLogos);

        SellerInfo savedSeller = sellerService.saveSellerInfo(sellerInfoDTO, fileInput);
        return ResponseEntity.ok(savedSeller);
    }
    
    
    @PutMapping("/update/{id}")
    public ResponseEntity<SellerInfo> updateSellerInfo(
            @PathVariable Long id,
            @RequestPart("seller") SellerInfoDTO sellerInfoDTO,
            @RequestPart(value = "idProofImages", required = false) List<MultipartFile> idProofImages,
            @RequestPart(value = "shopLogos", required = false) List<MultipartFile> shopLogos) {

        SellerFileInputModel fileInput = new SellerFileInputModel();
        fileInput.setIdProofImages(idProofImages);
        fileInput.setShopLogos(shopLogos);

        SellerInfo updatedSeller = sellerService.updateSellerInfo(id, sellerInfoDTO, fileInput);
        return ResponseEntity.ok(updatedSeller);
    }
    
	    @GetMapping("/seller/user/{userId}")
	    public ResponseEntity<SellerInfoDTO> getSellerDetailsByUserId(@PathVariable Integer userId) {
	        SellerInfoDTO response = sellerService.getSellerDetailsByUserId(userId);
	        return ResponseEntity.ok(response);
	    }
	    
	    @DeleteMapping("/delete/{sellerId}")
	    public ResponseEntity<String> deleteSeller(@PathVariable Long sellerId) {
	        try {
	            sellerService.deleteSellerById(sellerId);
	            return ResponseEntity.ok("Seller with ID " + sellerId + " deleted successfully.");
	        } catch (RuntimeException e) {
	            return ResponseEntity.status(404).body(e.getMessage());
	        }
	    }
}
