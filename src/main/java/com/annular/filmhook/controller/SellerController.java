package com.annular.filmhook.controller;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.SellerInfo;
import com.annular.filmhook.repository.SellerInfoRepository;
import com.annular.filmhook.service.SellerService;
import com.annular.filmhook.webmodel.SellerFileInputModel;
import com.annular.filmhook.webmodel.SellerInfoDTO;
import com.annular.filmhook.webmodel.SellerStatusUpdateDTO;

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
    public ResponseEntity<Response> saveSellerInfo(
            @RequestPart("seller") SellerInfoDTO sellerInfoDTO,
            @RequestPart(value = "idProofImages", required = false) List<MultipartFile> idProofImages,
            @RequestPart(value = "shopLogos", required = false) List<MultipartFile> shopLogos) {

        try {
            SellerFileInputModel fileInput = new SellerFileInputModel();
            fileInput.setIdProofImages(idProofImages);
            fileInput.setShopLogos(shopLogos);

            SellerInfo savedSeller = sellerService.saveSellerInfo(sellerInfoDTO, fileInput);

            Response response = Response.builder()
                    .status(200)
                    .message("Seller information saved successfully.")
                    .data(savedSeller)
                    .build();

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Response response = Response.builder()
                    .status(400)
                    .message(e.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            Response response = Response.builder()
                    .status(500)
                    .message("Internal server error: " + e.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updateSellerInfo(
            @PathVariable Long id,
            @RequestPart("seller") SellerInfoDTO sellerInfoDTO,
            @RequestPart(value = "idProofImages", required = false) List<MultipartFile> idProofImages,
            @RequestPart(value = "shopLogos", required = false) List<MultipartFile> shopLogos) {

        try {
            SellerFileInputModel fileInput = new SellerFileInputModel();
            fileInput.setIdProofImages(idProofImages);
            fileInput.setShopLogos(shopLogos);

            SellerInfo updatedSeller = sellerService.updateSellerInfo(id, sellerInfoDTO, fileInput);

            Response response = new Response(
                    200,
                    "Seller information updated successfully.",
                    updatedSeller
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Response response = new Response(
                    400,
                    "Failed to update seller info: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(400).body(response);

        } catch (Exception e) {
            Response response = new Response(
                    500,
                    "Internal server error: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<Response> getSellerDetailsByUserId(@PathVariable Integer userId) {
        try {
            SellerInfoDTO sellerInfo = sellerService.getSellerDetailsByUserId(userId);
            Response response = new Response(200, "Seller details fetched successfully.", sellerInfo);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Response response = new Response(404, "Seller not found for user ID: " + userId, null);
            return ResponseEntity.status(404).body(response);
        } catch (Exception e) {
            Response response = new Response(500, "Internal server error: " + e.getMessage(), null);
            return ResponseEntity.status(500).body(response);
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
    
    @PutMapping("/update-status")
    public ResponseEntity<Response> updateSellerStatus(@RequestBody SellerStatusUpdateDTO dto) {
        try {
            Optional<SellerInfo> optionalSeller = sellerInfoRepository.findById(dto.getSellerId());

            if (optionalSeller.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Response.builder()
                            .status(404)
                            .message("Seller not found with ID: " + dto.getSellerId())
                            .data(null)
                            .build()
                );
            }

            SellerInfo seller = optionalSeller.get();
            seller.setButtonStatus(dto.isButtonStatus());
            seller.setActiveStatus(dto.getActiveStatus());

            sellerInfoRepository.save(seller);
            sellerService.sendSellerStatusUpdateEmail(dto.getActiveStatus(), seller, dto.getReason() );

            return ResponseEntity.ok(
                Response.builder()
                        .status(200)
                        .message("Seller status updated successfully")
                        .data(seller)
                        .build()
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Response.builder()
                        .status(500)
                        .message("Error updating seller status: " + e.getMessage())
                        .data(null)
                        .build()
            );
        }
    }


}
