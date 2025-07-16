package com.annular.filmhook.controller;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.ShootingLocationBooking;
import com.annular.filmhook.model.ShootingLocationChat;
import com.annular.filmhook.model.ShootingLocationPayment;
import com.annular.filmhook.repository.ShootingLocationBookingRepository;
import com.annular.filmhook.repository.ShootingLocationPaymentRepository;
import com.annular.filmhook.service.ShootingLocationBookingService;
import com.annular.filmhook.util.HashGenerator;

import com.annular.filmhook.webmodel.ShootingLocationBookingDTO;
import com.annular.filmhook.webmodel.ShootingLocationChatDTO;
import com.annular.filmhook.webmodel.ShootingLocationPayURequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
	
    @Autowired
    private ShootingLocationPaymentRepository paymentRepo;

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

	        if (bookings.isEmpty()) {
	            logger.warn("No bookings found or client not valid: {}", clientId);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(new Response(0, "No bookings found or client not found", null));
	        }

	        logger.info("Fetched {} bookings for client ID: {}", bookings.size(), clientId);
	        return ResponseEntity.ok(new Response(1, "Client bookings retrieved successfully", bookings));

	    } catch (IllegalArgumentException e) {
	        logger.error("Invalid data: {}", e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new Response(0, "Invalid booking data: " + e.getMessage(), null));
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
	


	 @PostMapping("/send-booking-email")
	    public ResponseEntity<?> sendBookingEmail(@RequestBody ShootingLocationPayURequest request) {
	        try {
	            String txnid = request.getTxnid();
	            String status = request.getStatus();

	            logger.info("Sending booking email for txnid: {}, status: {}", txnid, status);

	            // Fetch payment by txnid
	            ShootingLocationPayment payment = paymentRepo.findByTxnid(txnid)
	                    .orElseThrow(() -> new RuntimeException("Payment not found for txnid: " + txnid));

	            // Get the booking from payment
	            ShootingLocationBooking booking = payment.getBooking();
	            if (booking == null) {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                        .body(new Response(0, "Booking not found for txnid: " + txnid, null));
	            }

	            boolean isSuccess = "SUCCESS".equalsIgnoreCase(status);

	            // Send emails
	            return bookingService.sendShootingLocationBookingEmail(booking, payment, isSuccess);

	        } catch (Exception e) {
	            logger.error("Error sending booking email", e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(new Response(0, "Failed to send booking email: " + e.getMessage(), null));
	        }
	    }
	
	

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

	@PostMapping("/send")
	public ResponseEntity<Response> sendMessage(
	        @RequestBody ShootingLocationChatDTO dto,
	        @RequestParam Integer propertyId 
	) {
	    try {
	        String result = bookingService.sendMessage(dto, propertyId);
	        return ResponseEntity.ok(new Response(1, result, null));
	    } catch (AccessDeniedException ade) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                .body(new Response(0, ade.getMessage(), null));
	    } catch (RuntimeException re) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new Response(0, re.getMessage(), null));
	    } catch (Exception ex) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new Response(0, "Unexpected error: " + ex.getMessage(), null));
	    }
	}

	@GetMapping("/can-chat")
	public ResponseEntity<Response> canChat(
	        @RequestParam Integer senderId,
	        @RequestParam Integer receiverId,
	        @RequestParam Integer propertyId) {
	    try {
	        boolean allowed = bookingService.canChatByProperty(senderId, receiverId, propertyId);

	        if (allowed) {
	            return ResponseEntity.ok(new Response(1, "Chat access granted", true));
	        } else {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                    .body(new Response(0, "Chat not allowed for this property", false));
	        }

	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                .body(new Response(0, "Invalid request: " + e.getMessage(), null));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new Response(0, "Error checking chat availability: " + e.getMessage(), null));
	    }
	}

	
	@GetMapping("/getMessages/{senderId}/{receiverId}")
	public ResponseEntity<List<ShootingLocationChatDTO>> getChatMessages(
	        @PathVariable Integer senderId,
	        @PathVariable Integer receiverId) {
	    List<ShootingLocationChatDTO> chatHistory = bookingService.getChatHistory(senderId, receiverId);
	    return ResponseEntity.ok(chatHistory);
	}

	

}
