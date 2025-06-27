package com.annular.filmhook.service.impl;

import com.annular.filmhook.Response;
import com.annular.filmhook.controller.ShootingLocationBookingController;

import com.annular.filmhook.converter.ShootingLocationBookingConverter;
import com.annular.filmhook.converter.ShootingLocationPaymentConverter;
import com.annular.filmhook.model.BookingStatus;
import com.annular.filmhook.model.PropertyAvailabilityDate;
import com.annular.filmhook.model.ShootingLocationBooking;
import com.annular.filmhook.model.ShootingLocationPayment;
import com.annular.filmhook.model.ShootingLocationPropertyDetails;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.PropertyAvailabilityDateRepository;
import com.annular.filmhook.repository.ShootingLocationBookingRepository;
import com.annular.filmhook.repository.ShootingLocationPaymentRepository;
import com.annular.filmhook.repository.ShootingLocationPropertyDetailsRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.ShootingLocationBookingService;
import com.annular.filmhook.util.HashGenerator;
import com.annular.filmhook.webmodel.ShootingLocationBookingDTO;
import com.annular.filmhook.webmodel.ShootingLocationPayURequest;
import com.google.api.client.util.Value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;



@Service
public class ShootingLocationBookingServiceImpl implements ShootingLocationBookingService {
	public static final Logger logger = LoggerFactory.getLogger(ShootingLocationBookingController.class);
	@Autowired
	private ShootingLocationBookingRepository bookingRepository;

	@Autowired
	private ShootingLocationPropertyDetailsRepository propertyRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private  ShootingLocationBookingRepository bookingRepo;
	@Autowired
	private ShootingLocationPaymentRepository paymentRepo;

	@Autowired
	private ShootingLocationPaymentConverter converter;

	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	 private PropertyAvailabilityDateRepository availabilityRepo;

	@Value("${payu.key}")
	private String key;

	@Value("${payu.salt}")
	private String salt;

	@Override
	public ShootingLocationBookingDTO createBooking(ShootingLocationBookingDTO dto) {
	    // Check if property exists
	    ShootingLocationPropertyDetails property = propertyRepository.findById(dto.getPropertyId())
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));

	    // Check if client exists
	    User client = userRepository.findById(dto.getClientId())
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

	    LocalDate newStart = dto.getShootStartDate();
	    LocalDate newEnd = dto.getShootEndDate();

	    // ✅ Step 1: Prevent overlapping CONFIRMED bookings for this property
	    List<ShootingLocationBooking> confirmedBookings = bookingRepository
	            .findByProperty_IdAndStatus(dto.getPropertyId(), BookingStatus.CONFIRMED);

	    for (ShootingLocationBooking existing : confirmedBookings) {
	        LocalDate existingStart = existing.getShootStartDate();
	        LocalDate existingEnd = existing.getShootEndDate();

	        boolean overlaps = !(newEnd.isBefore(existingStart) || newStart.isAfter(existingEnd));
	        if (overlaps) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
	                    "This property is already booked for selected dates.");
	        }
	    }

	    // ✅ Step 2: Allow same client to book again (on non-overlapping dates)
	    List<ShootingLocationBooking> clientBookings = bookingRepository
	            .findByProperty_IdAndClient_UserId(dto.getPropertyId(), dto.getClientId());

	    for (ShootingLocationBooking b : clientBookings) {
	        LocalDate clientStart = b.getShootStartDate();
	        LocalDate clientEnd = b.getShootEndDate();

	        boolean overlaps = !(newEnd.isBefore(clientStart) || newStart.isAfter(clientEnd));
	        if (overlaps) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
	                    "You already have a booking for this property on these dates.");
	        }
	    }

	    // ✅ Step 3: Save new booking
	    ShootingLocationBooking entity = ShootingLocationBookingConverter.toEntity(dto, client, property);
	    ShootingLocationBooking saved = bookingRepository.save(entity);

	    return ShootingLocationBookingConverter.toDTO(saved);
	}

	@Override
	public List<ShootingLocationBookingDTO> getBookingsByClient(Integer clientId) {
		return bookingRepository.findByClient_UserId(clientId)
				.stream()
				.map(ShootingLocationBookingConverter::toDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<ShootingLocationBookingDTO> getBookingsByProperty(Integer propertyId) {
		return bookingRepository.findByProperty_Id(propertyId)
				.stream()
				.map(ShootingLocationBookingConverter::toDTO)
				.collect(Collectors.toList());
	}

	

	@Override
	public ResponseEntity<?> saveShootingPayment(ShootingLocationPayURequest req) {
		try {
			ShootingLocationBooking booking = bookingRepo.findById(req.getBookingId())
					.orElseThrow(() -> new RuntimeException("Booking not found"));

			String txnid = (req.getTxnid() == null || req.getTxnid().isEmpty())
					? "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase()
							: req.getTxnid();

			if (paymentRepo.existsByTxnid(txnid)) {
				return ResponseEntity.badRequest().body(new Response(0, "Duplicate transaction ID", null));
			}

			String hash = HashGenerator.generateHash(
					key,
					txnid,
					req.getAmount(),
					req.getProductinfo(),
					req.getFirstname(),
					req.getEmail(),
					salt
					);

			ShootingLocationPayment payment = ShootingLocationPayment.builder()
					.txnid(txnid)
					.amount(req.getAmount())
					.productinfo(req.getProductinfo())
					.firstname(req.getFirstname())
					.email(req.getEmail())
					.phone(req.getPhone())
					.status("PENDING")
					.paymentHash(hash)
					.booking(booking)
					.build();

			payment=paymentRepo.save(payment);
			return ResponseEntity.ok(new Response(1, "Payment saved successfully", converter.toDto(payment)));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(0, "Error saving payment: " + e.getMessage(), null));
		}
	}

	@Override
	public ResponseEntity<?> sendShootingLocationBookingMail(ShootingLocationPayURequest request) {
		try {
			String status = request.getStatus(); 
			String txnid = request.getTxnid();

			// Fetch payment record
			ShootingLocationPayment payment = paymentRepo.findByTxnid(txnid)
					.orElseThrow(() -> new RuntimeException("Payment not found with txnid: " + txnid));

			ShootingLocationBooking booking = payment.getBooking();

			// Set statuses
			if ("SUCCESS".equalsIgnoreCase(status)) {
				payment.setStatus("SUCCESS");
				booking.setStatus(BookingStatus.CONFIRMED); 
			} else {
				payment.setStatus("FAILURE");
				booking.setStatus(BookingStatus.CANCELLED); 
			}

			// Save updates
			paymentRepo.save(payment);
			bookingRepo.save(booking);

			// Prepare mail
			String to = payment.getEmail();
			String subject = "🎬 Shooting Location Booking - " + (status.equalsIgnoreCase("SUCCESS") ? "Confirmed ✅" : "Failed ❌");

			StringBuilder content = new StringBuilder();
			content.append("<html><body>");
			content.append("<h3>Hello ").append(payment.getFirstname()).append(",</h3>");
			if ("SUCCESS".equalsIgnoreCase(status)) {
				content.append("<p>🎉 Your payment was successful and the shooting location booking is now <b>confirmed</b>.</p>");
			} else {
				content.append("<p>⚠️ Your payment failed. The shooting location booking is <b>not confirmed</b>.</p>");
			}
			content.append("<p><b>Txn ID:</b> ").append(txnid).append("</p>");
			content.append("<p><b>Tota Amount:</b> ₹").append(payment.getAmount()).append("</p>");
			content.append("<br><p>Thank you for choosing FilmHook.</p>");
			content.append("</body></html>");

			// Send email
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(content.toString(), true);
			javaMailSender.send(message);

			return ResponseEntity.ok(new Response(1, "Email sent and booking updated", null));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(0, "Error sending email: " + e.getMessage(), null));
		}
	}


	@Scheduled(cron = "0 0 17 * * *") // Every day at 5:00 PM
	public void sendBookingExpiryReminders() {
	    LocalDate tomorrow = LocalDate.now().plusDays(1);
	    List<ShootingLocationBooking> bookings = bookingRepo.findByShootEndDate(tomorrow);

	    logger.info("Found {} bookings ending tomorrow ({})", bookings.size(), tomorrow);

	    for (ShootingLocationBooking booking : bookings) {
	        // ✅ Check if payment exists and is marked as SUCCESS
	        Optional<ShootingLocationPayment> paymentOpt = paymentRepo.findByBooking_IdAndStatus(booking.getId(), "SUCCESS");

	        if (paymentOpt.isPresent()) {
	            try {
	                sendReminderEmail(booking);
	                logger.info("✅ Reminder email sent to {} for booking {}", booking.getClient().getEmail(), booking.getId());
	            } catch (Exception ex) {
	                logger.error("❌ Failed to send reminder for booking {}: {}", booking.getId(), ex.getMessage(), ex);
	            }
	        } else {
	            logger.info("⏭️ Skipping booking {} – no successful payment found", booking.getId());
	        }
	    }
	}


    private void sendReminderEmail(ShootingLocationBooking booking) throws MessagingException {
        String to = booking.getClient().getEmail();
        String name = booking.getClient().getName();
        String subject = "⏳ Reminder: Your shoot at “" + booking.getProperty().getPropertyName() + "” ends tomorrow";
        String html =
            "<html><body>" +
            "<p>Hi " + name + ",</p>" +
            "<p>This is a friendly reminder that your booking at <b>" +
            booking.getProperty().getPropertyName() +
            "</b> will end on <b>" + booking.getShootEndDate() + "</b>.</p>" +
            "<p>Thank you for choosing FilmHook!</p>" +
            "</body></html>";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true); 
        javaMailSender.send(message);
    }

    @Scheduled(cron = "0 30 0 * * *") // Every day at 12:30 AM
    public void markBookingsAsCompleted() {
        LocalDate today = LocalDate.now();
     
   List<ShootingLocationBooking> expiredBookings = bookingRepo.findByShootEndDateLessThanEqualAndStatus(today, BookingStatus.CONFIRMED);

        logger.info("Found {} CONFIRMED bookings to mark as COMPLETED", expiredBookings.size());

        for (ShootingLocationBooking booking : expiredBookings) {
            try {
                booking.setStatus(BookingStatus.COMPLETED);
                bookingRepo.save(booking);
                logger.info("✅ Booking ID {} marked as COMPLETED", booking.getId());
                // Send email notification
                sendCompletionEmail(booking);
            } catch (Exception ex) {
                logger.error("❌ Error updating booking {}: {}", booking.getId(), ex.getMessage(), ex);
            }
        }}
    
        private void sendCompletionEmail(ShootingLocationBooking booking) throws MessagingException {
            String to = booking.getClient().getEmail();
            String name = booking.getClient().getName();
            String property = booking.getProperty().getPropertyName();

            String subject = "📸 Booking Completed - Thank You for Choosing FilmHook!";
            String html = "<html><body>" +
                    "<p>Hi " + name + ",</p>" +
                    "<p>We hope your shoot at <b>" + property + "</b> was a success! 🎬</p>" +
                    "<p>Your booking has now been marked as <b>COMPLETED</b> as of <b>" + booking.getShootEndDate() + "</b>.</p>" +
                    "<p>Thank you for using <b>FilmHook</b>. We look forward to serving you again!</p>" +
                    "<p>📧 <a href='mailto:support@film-hookapps.com'>Contact Support</a> | 🌐 <a href='https://film-hookapps.com/'>Visit Website</a></p>" +
                    "</body></html>";

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            javaMailSender.send(message);

            logger.info("📩 Completion email sent to {}", to);
        }

        @Override
        public List<LocalDate> getAvailableDatesForProperty(Integer propertyId) {

            // Step 1: Fetch all availability ranges for the property
            List<PropertyAvailabilityDate> availabilityRanges = availabilityRepo.findByProperty_Id(propertyId);

            // Step 2: Expand each availability range into individual dates
            Set<LocalDate> ownerAvailableDates = new HashSet<>();
            for (PropertyAvailabilityDate range : availabilityRanges) {
                LocalDate current = range.getStartDate();
                while (!current.isAfter(range.getEndDate())) {
                    ownerAvailableDates.add(current);
                    current = current.plusDays(1);
                }
            }

            // Step 3: Get all confirmed bookings for this property
            List<ShootingLocationBooking> confirmedBookings =
                    bookingRepo.findByProperty_IdAndStatus(propertyId, BookingStatus.CONFIRMED);

            // Step 4: Collect booked dates
            Set<LocalDate> bookedDates = new HashSet<>();
            for (ShootingLocationBooking booking : confirmedBookings) {
                LocalDate current = booking.getShootStartDate();
                while (!current.isAfter(booking.getShootEndDate())) {
                    bookedDates.add(current);
                    current = current.plusDays(1);
                }
            }

            // Step 5: Filter out booked dates from owner's available dates
            ownerAvailableDates.removeAll(bookedDates);
            
            LocalDate today = LocalDate.now();
            
            // Step 6: Return sorted available dates
            return ownerAvailableDates.stream()
            		.sorted()
            		.filter(date -> !bookedDates.contains(date)) // not booked
                    .filter(date -> !date.isBefore(today))       // not in past
                    .collect(Collectors.toList());
        }



}







