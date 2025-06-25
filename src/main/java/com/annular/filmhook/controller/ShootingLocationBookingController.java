package com.annular.filmhook.controller;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.ShootingLocationBookingService;
import com.annular.filmhook.webmodel.ShootingLocationBookingDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
public class ShootingLocationBookingController {

    private static final Logger logger = LoggerFactory.getLogger(ShootingLocationBookingController.class);

    @Autowired
    private ShootingLocationBookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<Response> createBooking(@RequestBody ShootingLocationBookingDTO dto) {
        try {
            logger.info("Creating booking for property ID: {}", dto.getPropertyId());
            ShootingLocationBookingDTO created = bookingService.createBooking(dto);
            return ResponseEntity.ok(new Response(1, "Booking created successfully", created));
        } catch (ResponseStatusException e) {
           
            logger.warn("Booking creation failed: {}", e.getReason());
            return ResponseEntity.status(e.getStatus())
                    .body(new Response(0, e.getReason(), null));
        } catch (Exception e) {
           
            logger.error("Unexpected error during booking creation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(0, "Unexpected error occurred", null));
        }
    }
    @GetMapping("/client/{clientId}")
    public ResponseEntity<Response> getBookingsByClient(@PathVariable Integer clientId) {
        try {
            logger.info("Fetching bookings for client ID: {}", clientId);
            List<ShootingLocationBookingDTO> bookings = bookingService.getBookingsByClient(clientId);
            logger.info("Fetched {} bookings for client ID: {}", bookings.size(), clientId);
            return ResponseEntity.ok(new Response(1, "Client bookings retrieved successfully", bookings));
        } catch (Exception e) {
            logger.error("Error while retrieving bookings by client ID: {}", clientId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(-1, "Error retrieving client bookings", null));
        }
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<Response> getBookingsByProperty(@PathVariable Integer propertyId) {
        try {
            logger.info("Fetching bookings for property ID: {}", propertyId);
            List<ShootingLocationBookingDTO> bookings = bookingService.getBookingsByProperty(propertyId);
            logger.info("Fetched {} bookings for property ID: {}", bookings.size(), propertyId);
            return ResponseEntity.ok(new Response(1, "Property bookings retrieved successfully", bookings));
        } catch (Exception e) {
            logger.error("Error while retrieving bookings by property ID: {}", propertyId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(-1, "Error retrieving property bookings", null));
        }
    }
}
