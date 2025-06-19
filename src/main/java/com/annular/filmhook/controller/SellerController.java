package com.annular.filmhook.controller;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.SellerInfo;
import com.annular.filmhook.service.SellerService;
import com.annular.filmhook.webmodel.SellerFileInputModel;
import com.annular.filmhook.webmodel.SellerInfoDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
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


    @DeleteMapping("/delete/{sellerId}")
    public ResponseEntity<Response> deleteSeller(@PathVariable Long sellerId) {
        try {
            sellerService.deleteSellerById(sellerId);
            Response response = new Response(200, "Seller with ID " + sellerId + " deleted successfully.", null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Response response = new Response(404, e.getMessage(), null);
            return ResponseEntity.status(404).body(response);
        } catch (Exception e) {
            Response response = new Response(500, "Internal server error: " + e.getMessage(), null);
            return ResponseEntity.status(500).body(response);
        }
    }
}
