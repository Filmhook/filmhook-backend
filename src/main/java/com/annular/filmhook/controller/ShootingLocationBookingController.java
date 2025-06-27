package com.annular.filmhook.controller;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.ShootingLocationBooking;
import com.annular.filmhook.repository.ShootingLocationBookingRepository;
import com.annular.filmhook.service.ShootingLocationBookingService;
import com.annular.filmhook.util.HashGenerator;

import com.annular.filmhook.webmodel.ShootingLocationBookingDTO;
import com.annular.filmhook.webmodel.ShootingLocationPayURequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/booking")
public class ShootingLocationBookingController {

	private static final Logger logger = LoggerFactory.getLogger(ShootingLocationBookingController.class);

	@Autowired
	private ShootingLocationBookingService bookingService;

	@Autowired
	private ShootingLocationBookingRepository bookingRepo;

	@Autowired
	private HashGenerator hashGenerator;

	@PostMapping("/create")
	public ResponseEntity<Response> createBooking(@RequestBody ShootingLocationBookingDTO dto) {
		try {      	      
			List<ShootingLocationBooking> overlaps = bookingRepo.findOverlappingBookings(
					dto.getPropertyId(), dto.getShootStartDate(), dto.getShootEndDate());

			if (!overlaps.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Selected dates are already booked.");
			}
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

	@PostMapping("/savePayment")
	public ResponseEntity<?> savePayment(@RequestBody ShootingLocationPayURequest request) {
		return bookingService.saveShootingPayment(request);
	}

	@PostMapping("/send-booking-mail")
	public ResponseEntity<?> sendBookingMail(@RequestBody ShootingLocationPayURequest request) {
		try {
			logger.info("Sending booking mail for txnid: {}", request.getTxnid());
			return bookingService.sendShootingLocationBookingMail(request);
		} catch (Exception e) {
			logger.error("Error sending booking confirmation mail", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(0, "Failed to send booking mail: " + e.getMessage(), null));
		}}
	

	@PostMapping("/trigger/reminder-tomorrow")
	public ResponseEntity<Response> triggerTomorrowReminders() {
	    try {
	        bookingService.sendBookingExpiryReminders(); // Calls the reminder job
	        return ResponseEntity.ok(new Response(1, "Tomorrow's reminders triggered successfully", null));
	    } catch (Exception ex) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new Response(0, "Failed to trigger reminders: " + ex.getMessage(), null));
	    }
	}

	@PostMapping("/trigger/mark-completed")
	public ResponseEntity<Response> triggerCompletion() {
	    try {
	        bookingService.markBookingsAsCompleted(); // Calls the completion job
	        return ResponseEntity.ok(new Response(1, "Completed bookings updated successfully", null));
	    } catch (Exception ex) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new Response(0, "Failed to mark bookings as completed: " + ex.getMessage(), null));
	    }
	}
	
	@GetMapping("/availability/{propertyId}")
	public ResponseEntity<Response> getAvailableDates(@PathVariable Integer propertyId) {
	    try {
	        List<LocalDate> availableDates = bookingService.getAvailableDatesForProperty(propertyId);
	        return ResponseEntity.ok(new Response(1, "Available dates fetched", availableDates));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new Response(0, "Error fetching availability: " + e.getMessage(), null));
	    }
	}



}
