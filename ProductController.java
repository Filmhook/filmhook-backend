package com.annular.filmhook.controller;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annular.filmhook.model.Product;
import com.annular.filmhook.service.impl.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService service;
    public ProductController(ProductService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody Product product) {
        if (service.findByBarcode(product.getBarcodeNumber()).isPresent()) {
            return ResponseEntity.badRequest().body("Product with barcode exists");
        }
        Product saved = service.save(product);
        return ResponseEntity.ok(saved);
    }

    // ✔ GET PRODUCT DETAILS (JSON)
    @GetMapping("/details/{barcode}")
    public ResponseEntity<?> getByBarcode(@PathVariable String barcode) {
        return service.findByBarcode(barcode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping(value = "/barcode-image/{code}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getBarcode(@PathVariable String code) {
        try {
            byte[] png = service.generateCode128(code);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return ResponseEntity.ok().headers(headers).body(png);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}