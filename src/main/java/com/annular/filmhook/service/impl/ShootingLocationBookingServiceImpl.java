package com.annular.filmhook.service.impl;

import com.annular.filmhook.Response;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import com.annular.filmhook.controller.ShootingLocationBookingController;

import com.annular.filmhook.converter.ShootingLocationBookingConverter;
import com.annular.filmhook.converter.ShootingLocationPaymentConverter;
import com.annular.filmhook.model.BookingStatus;
import com.annular.filmhook.model.PropertyAvailabilityDate;
import com.annular.filmhook.model.ShootingLocationBooking;
import com.annular.filmhook.model.ShootingLocationChat;
import com.annular.filmhook.model.ShootingLocationPayment;
import com.annular.filmhook.model.ShootingLocationPropertyDetails;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.PropertyAvailabilityDateRepository;
import com.annular.filmhook.repository.ShootingLocationBookingRepository;
import com.annular.filmhook.repository.ShootingLocationChatRepository;
import com.annular.filmhook.repository.ShootingLocationPaymentRepository;
import com.annular.filmhook.repository.ShootingLocationPropertyDetailsRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.ShootingLocationBookingService;
import com.annular.filmhook.util.HashGenerator;
import com.annular.filmhook.webmodel.ShootingLocationBookingDTO;
import com.annular.filmhook.webmodel.ShootingLocationChatDTO;
import com.annular.filmhook.webmodel.ShootingLocationPayURequest;
import com.google.api.client.util.Value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;


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
	
	@Autowired
    private ShootingLocationChatRepository chatRepo;

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
// My order
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

        // Fetch payment and booking
        ShootingLocationPayment payment = paymentRepo.findByTxnid(txnid)
                .orElseThrow(() -> new RuntimeException("Payment not found with txnid: " + txnid));
        ShootingLocationBooking booking = payment.getBooking();

        if (payment == null || booking == null) {
            throw new RuntimeException("Payment or booking not found");
        }

        boolean isSuccess = "SUCCESS".equalsIgnoreCase(status);

        // Update status
        payment.setStatus(isSuccess ? "SUCCESS" : "FAILURE");
        booking.setStatus(isSuccess ? BookingStatus.CONFIRMED : BookingStatus.CANCELLED);
        paymentRepo.save(payment);
        bookingRepo.save(booking);

        // Common booking details
        String locationName = booking.getProperty().getPropertyName();
        String checkIn = booking.getShootStartDate().toString();
        String checkOut = booking.getShootEndDate().toString();
        String amount = "₹" + payment.getAmount();
        String paymentStatus = payment.getStatus().trim();
        String customerName = payment.getFirstname();
        String customerEmail = payment.getEmail();

        // === 1. Prepare Client Email ===
        String clientSubject = isSuccess
                ? "🎬 Your Shooting Location Booking is Confirmed"
                : "Payment Failed - Booking Not Confirmed";

        StringBuilder clientContent = new StringBuilder();
        clientContent.append("<html><body style='font-family:Arial,sans-serif;'>")
                .append("<div style='padding:20px; border:1px solid #ddd; border-radius:6px;'>")
                .append("<h2 style='color:#2956b8;'>Shooting Location ").append(isSuccess ? "Confirmation✅" : "Failure ❌").append("</h2>")
                .append("<p>Hello <strong>").append(customerName).append("</strong>,</p>");

        if (isSuccess) {
            clientContent.append("<p>🎉 Your booking has been <strong>successfully confirmed</strong>.</p>");
        } else {
            clientContent.append("<p>⚠️ Unfortunately, your payment <strong>failed</strong>. Your booking is not confirmed.</p>");
        }

        clientContent.append("<h4 style='margin-top:20px; border-bottom:1px solid #ccc;'>Booking Details:</h4>")
                .append("<p>")
                .append("<b>Location:</b> ").append(locationName).append("<br>")
                .append("<b>Check-in:</b> ").append(checkIn).append("<br>")
                .append("<b>Check-out:</b> ").append(checkOut).append("<br>")
                .append("<b>Total Amount:</b> ").append(amount).append("<br>")
                .append("<b>Status:</b> ").append(paymentStatus)
                .append("</p>");

        if (isSuccess) {
            clientContent.append("<p>📎 Invoice is attached for your reference.</p>");
        } else {
            clientContent.append("<p>Please try again or contact support if you need help.</p>");
        }

        clientContent.append("<p style='margin-top:30px;'>Best regards,<br><strong>FilmHook Team</strong><br>")
                .append("<small>support@filmhook.com | +91-9876543xxx</small></p>")
                .append("</div></body></html>");

        // === Send Client Email ===
        MimeMessage clientMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper clientHelper = new MimeMessageHelper(clientMessage, true);
        clientHelper.setTo(customerEmail);
        clientHelper.setSubject(clientSubject);
        clientHelper.setText(clientContent.toString(), true);

        if (isSuccess) {
            byte[] invoiceBytes = generateInvoicePdf(payment, booking);
            DataSource attachment = new ByteArrayDataSource(invoiceBytes, "application/pdf");
            clientHelper.addAttachment("Invoice_" + txnid + ".pdf", attachment);
        }

        javaMailSender.send(clientMessage);

        // === 2. Prepare and Send Owner Email (only if success) ===
        if (isSuccess) {
            User owner = booking.getProperty().getUser();
            if (owner != null && owner.getEmail() != null) {
                String ownerName = owner.getName();
                if (ownerName != null && !ownerName.isEmpty()) {
                    ownerName = Arrays.stream(ownerName.trim().split("\\s+"))
                            .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1).toLowerCase())
                            .collect(Collectors.joining(" "));
                }

                String ownerSubject = "Your Property Has Been Booked!";
                StringBuilder ownerContent = new StringBuilder();
                ownerContent.append("<html><body style='font-family:Arial,sans-serif;'>")
                        .append("<div style='max-width:600px; margin:0 auto; padding:20px; border:1px solid #e0e0e0; border-radius:8px; background-color:#f9f9f9;'>")
                        .append("<h2 style='color:#1a73e8; font-size:22px; margin-bottom:10px;'>📢 New Booking Alert</h2>")
                        .append("<p style='font-size:16px;'>Dear <strong>").append(ownerName).append("</strong>,</p>")
                        .append("<p style='font-size:15px; line-height:1.6;'>")
                        .append("We are pleased to inform you that your property <strong>")
                        .append(locationName)
                        .append("</strong> has been successfully booked by <strong>")
                        .append(customerName)
                        .append("</strong>.</p>")
                        .append("<h4 style='color:#444; font-size:16px; margin-top:25px; border-bottom:1px solid #ccc; padding-bottom:5px;'>📋 Booking Details</h4>")
                        .append("<table style='width:100%; font-size:14px; line-height:1.6;'>")
                        .append("<tr><td style='width:150px;'><strong>Check-in:</strong></td><td>").append(checkIn).append("</td></tr>")
                        .append("<tr><td><strong>Check-out:</strong></td><td>").append(checkOut).append("</td></tr>")
                        .append("<tr><td><strong>Client Email:</strong></td><td>").append(customerEmail).append("</td></tr>")
                        .append("<tr><td><strong>Total Amount:</strong></td><td>").append(amount).append("</td></tr>")
                        .append("</table>")
                        .append("<p style='margin-top:30px; font-size:14px;'>")
                        .append("Please ensure the property is prepared and ready for the scheduled booking. If you have any questions, feel free to contact our support team.")
                        .append("</p>")
                        .append("<p style='margin-top:30px; font-size:14px;'>")
                        .append("Best regards,<br><strong>FilmHook Team</strong><br>")
                        .append("<span style='color:#888;'>support@filmhook.com | +91-9876543xxx</span>")
                        .append("</p>")
                        .append("</div></body></html>");

                MimeMessage ownerMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper ownerHelper = new MimeMessageHelper(ownerMessage, true);
                ownerHelper.setTo(owner.getEmail());
                ownerHelper.setSubject(ownerSubject);
                ownerHelper.setText(ownerContent.toString(), true);

                javaMailSender.send(ownerMessage);
                return ResponseEntity.ok(new Response(1, "Emails sent to client and owner", null));
            } else {
                return ResponseEntity.ok(new Response(1, "Client email sent. Owner email not sent (missing details)", null));
            }
        }

        // If payment failed
        return ResponseEntity.ok(new Response(1, "Client email sent (payment failed)", null));

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response(0, "Error sending emails: " + e.getMessage(), null));
    }
}


	private byte[] generateInvoicePdf(ShootingLocationPayment payment, ShootingLocationBooking booking) {
	    try {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        PdfWriter writer = new PdfWriter(baos);
	        PdfDocument pdf = new PdfDocument(writer);
	        Document doc = new Document(pdf, PageSize.A4);
	        doc.setMargins(36, 36, 36, 36);

	        DeviceRgb blue = new DeviceRgb(41, 86, 184);
	        final int fontSize = 10;

	        int days = (int) ChronoUnit.DAYS.between(booking.getShootStartDate(), booking.getShootEndDate()) + 1;
	        double rate = booking.getPricePerDay() != null ? booking.getPricePerDay() : 0.0;
	        double base = booking.getBaseAmount() != null ? booking.getBaseAmount() : 0.0;
	        double gst = booking.getGstAmount() != null ? booking.getGstAmount() : 0.0;
	        double total = booking.getTotalAmount() != null ? booking.getTotalAmount() : base + gst;

	        // ✅ Load logo from classpath
	        InputStream logoStream = getClass().getClassLoader().getResourceAsStream("static/images/logo.jpeg");
	        if (logoStream == null) throw new RuntimeException("Logo image not found in classpath");
	        Image logo = new Image(ImageDataFactory.create(logoStream.readAllBytes()))
	                .scaleToFit(120, 60)
	                .setHorizontalAlignment(HorizontalAlignment.CENTER)
	                .setMarginBottom(8);
	        doc.add(logo);

	        doc.add(new Paragraph("TAX INVOICE")
	                .setTextAlignment(TextAlignment.CENTER)
	                .setFontSize(14)
	                .setBold()
	                .setFontColor(blue)
	                .setMarginBottom(10));

	        // Company info
	        Table header = new Table(UnitValue.createPercentArray(new float[]{70, 30}))
	                .setWidth(UnitValue.createPercentValue(100));
	        header.addCell(new Cell()
	                .add(new Paragraph("FilmHook Pvt. Ltd.")
	                        .setBold().setFontSize(13).setFontColor(blue))
	                .add(new Paragraph("Bangalore\nGSTIN: 29ABCDE1234F2Z5\nEmail: support@filmhook.com\nPhone: +91-9876543210")
	                        .setFontSize(fontSize))
	                .setBorder(Border.NO_BORDER));
	        header.addCell(new Cell().setBorder(Border.NO_BORDER)); // empty
	        doc.add(header);

	        // Order Info
	        Table orderInfo = new Table(UnitValue.createPercentArray(new float[]{33, 33, 33}))
	                .setWidth(UnitValue.createPercentValue(100))
	                .setMarginTop(15);
	        orderInfo.addCell(getLightCell("Order No"));
	        orderInfo.addCell(getLightCell("Date"));
	        orderInfo.addCell(getLightCell("Customer ID"));
	        orderInfo.addCell(getPlainCell("INV-" + payment.getTxnid()));
	        orderInfo.addCell(getPlainCell(LocalDate.now().toString()));
	        orderInfo.addCell(getPlainCell(payment.getEmail()));
	        doc.add(orderInfo);

	        doc.add(new Paragraph("\nBill To")
	                .setFontSize(fontSize)
	                .setBold()
	                .setMarginTop(8));
	        doc.add(new Paragraph("Name: " + payment.getFirstname())
	                .setFontSize(fontSize)
	                .setMarginBottom(10));

	        // Charges Table
	        Table charges = new Table(UnitValue.createPercentArray(new float[]{40, 20, 20, 20}))
	                .setWidth(UnitValue.createPercentValue(100))
	                .setMarginTop(10);

	        charges.addHeaderCell(getStyledBottomBorderHeader("Description"));
	        charges.addHeaderCell(getStyledBottomBorderHeader("Days"));
	        charges.addHeaderCell(getStyledBottomBorderHeader("Rate/Day"));
	        Cell totalHeader = getStyledBottomBorderHeader("Amount");
	        totalHeader.setTextAlignment(TextAlignment.RIGHT);
	        charges.addHeaderCell(totalHeader);

	        charges.addCell(getStyledBottomBorderCell("Shooting Location Property: " + booking.getProperty().getPropertyName()));
	        charges.addCell(getStyledBottomBorderCell(String.valueOf(days)));
	        charges.addCell(getStyledBottomBorderCell("₹ " + String.format("%.2f", rate)));
	        Cell baseCell = getStyledBottomBorderCell("₹ " + String.format("%.2f", base));
	        baseCell.setTextAlignment(TextAlignment.RIGHT);
	        charges.addCell(baseCell);

	        // Applied Tax
	        Cell taxLabel = new Cell(1, 3)
	                .add(new Paragraph("\nApplied Tax").setBold().setUnderline().setFontSize(9))
	                .add(new Paragraph("(18% GST Included)").setFontSize(8))
	                .setBorder(Border.NO_BORDER);
	        Cell taxValue = new Cell()
	                .add(new Paragraph("₹ " + String.format("%.2f", gst))
	                        .setTextAlignment(TextAlignment.RIGHT).setFontSize(9))
	                .setBorder(Border.NO_BORDER);
	        charges.addCell(taxLabel);
	        charges.addCell(taxValue);

	        // Total Invoice
	        Cell totalLabel = new Cell(1, 3)
	                .add(new Paragraph("Total Invoice Value")
	                        .setFontColor(blue)
	                        .setBold().setFontSize(10))
	                .setBorderTop(new SolidBorder(ColorConstants.GRAY, 0.5f))
	                .setBorder(Border.NO_BORDER);
	        Cell totalAmount = new Cell()
	                .add(new Paragraph("₹ " + String.format("%.2f", total))
	                        .setFontSize(10)
	                        .setBold()
	                        .setFontColor(blue)
	                        .setTextAlignment(TextAlignment.RIGHT))
	                .setBorderTop(new SolidBorder(ColorConstants.GRAY, 0.5f))
	                .setBorder(Border.NO_BORDER);
	        charges.addCell(totalLabel);
	        charges.addCell(totalAmount);

	        doc.add(charges);

	        doc.add(new Paragraph("\nDeclaration")
	                .setBold()
	                .setFontSize(12)
	                .setMarginTop(20));
	        doc.add(new Paragraph("We declare that this invoice shows the actual price of the services provided and that all particulars are true and correct.")
	                .setFontSize(fontSize));

	        // ✅ Load signature from classpath
	        InputStream signStream = getClass().getClassLoader().getResourceAsStream("static/images/Signature.jpeg");
	        if (signStream == null) throw new RuntimeException("Signature image not found in classpath");
	        Image sign = new Image(ImageDataFactory.create(signStream.readAllBytes()))
	                .scaleToFit(80, 30);
	        Paragraph signText = new Paragraph("For FilmHook Pvt. Ltd\n(Authorized Signatory)")
	                .setFontSize(9)
	                .setTextAlignment(TextAlignment.RIGHT);
	        Paragraph signBlock = new Paragraph().add(sign).add("\n").add(signText);
	        Table signTable = new Table(1).setWidth(UnitValue.createPercentValue(100)).setMarginTop(30);
	        signTable.addCell(new Cell().add(signBlock)
	                .setBorder(Border.NO_BORDER)
	                .setTextAlignment(TextAlignment.RIGHT));
	        doc.add(signTable);

	        doc.close();
	        return baos.toByteArray();

	    } catch (Exception e) {
	        throw new RuntimeException("Failed to generate invoice PDF", e);
	    }
	}
	private Cell getLightCell(String text) {
	    return new Cell().add(new Paragraph(text).setBold().setFontSize(9))
	            .setBackgroundColor(new DeviceRgb(245, 245, 245))
	            .setPadding(4);
	}

	private Cell getPlainCell(String text) {
	    return new Cell().add(new Paragraph(text).setFontSize(9)).setPadding(5);
	}

	private Cell getStyledBottomBorderHeader(String text) {
	    return new Cell()
	            .add(new Paragraph(text).setBold().setFontSize(10))
	            .setBorderTop(Border.NO_BORDER)
	            .setBorderLeft(Border.NO_BORDER)
	            .setBorderRight(Border.NO_BORDER)
	            .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
	}

	private Cell getStyledBottomBorderCell(String text) {
	    return new Cell()
	            .add(new Paragraph(text).setFontSize(9))
	            .setBorderTop(Border.NO_BORDER)
	            .setBorderLeft(Border.NO_BORDER)
	            .setBorderRight(Border.NO_BORDER)
	            .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
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


	 @Override
	    public boolean canChatByProperty(Integer senderId, Integer receiverId, Integer propertyId) {
	        LocalDateTime now = LocalDateTime.now();

	        List<ShootingLocationBooking> bookings = bookingRepository.findBookingsBetweenUsersAndProperty(
	                senderId, receiverId, propertyId);

	        for (ShootingLocationBooking booking : bookings) {
	            Integer clientId = booking.getClient().getUserId();
	            Integer ownerId = booking.getProperty().getUser().getUserId();

	            boolean isValidPair = 
	                (senderId.equals(clientId) && receiverId.equals(ownerId)) ||
	                (senderId.equals(ownerId) && receiverId.equals(clientId));

	            if (!isValidPair) continue;

	            Optional<ShootingLocationPayment> paymentOpt = 
	                paymentRepo.findByBooking_IdAndStatus(booking.getId(), "SUCCESS");

	            if (paymentOpt.isPresent()) {
	                LocalDateTime paymentTime = paymentOpt.get().getCreatedOn();
	                LocalDateTime shootEndTime = booking.getShootEndDate().atTime(23, 59);

	                if (now.isAfter(paymentTime) && now.isBefore(shootEndTime)) {
	                    return true;
	                }
	            }
	        }

	        return false;
	    }


	 @Override
	 public String sendMessage(ShootingLocationChatDTO dto, Integer propertyId) {
	     if (!canChatByProperty(dto.getSenderId(), dto.getReceiverId(), propertyId)) {
	         throw new AccessDeniedException("Chat not allowed for this property.");
	     }

	     ShootingLocationBooking booking = findBookingForChat(dto.getSenderId(), dto.getReceiverId(), propertyId);
	     if (booking == null) {
	         throw new RuntimeException("Booking with payment not found for chat.");
	     }

	     ShootingLocationChat chat = new ShootingLocationChat();
	     chat.setShootingLocationSenderId(dto.getSenderId());
	     chat.setShootingLocationReceiverId(dto.getReceiverId());
	     chat.setMessage(dto.getMessage());
	     chat.setTimeStamp(new Date());
	     chat.setBooking(booking);

	     chatRepo.save(chat);
	     return "Message sent";
	 }

	private ShootingLocationBooking findBookingForChat(Integer senderId, Integer receiverId, Integer propertyId) {
	    List<ShootingLocationBooking> bookings = bookingRepository.findBookingsBetweenUsers(senderId, receiverId);
	    for (ShootingLocationBooking booking : bookings) {
	        Optional<ShootingLocationPayment> paymentOpt = paymentRepo.findByBooking_IdAndStatus(booking.getId(), "SUCCESS");
	        if (paymentOpt.isPresent()) {
	            LocalDateTime now = LocalDateTime.now();
	            LocalDateTime paymentTime = paymentOpt.get().getCreatedOn();
	            LocalDateTime endTime = booking.getShootEndDate().atTime(23, 59);
	            if (now.isAfter(paymentTime) && now.isBefore(endTime)) {
	                return booking;
	            }
	        }
	    }
	    return null;
	}

	@Override
	public List<ShootingLocationChatDTO> getChatHistory(Integer senderId, Integer receiverId) {
	    List<ShootingLocationChat> chats = chatRepo.getChatHistoryBetweenUsers(senderId, receiverId);
	    return chats.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	private ShootingLocationChatDTO convertToDTO(ShootingLocationChat chat) {
	    return ShootingLocationChatDTO.builder()
	    		
	            .chatId(chat.getShootingLocationChatId())
	            .senderId(chat.getShootingLocationSenderId())
	            .receiverId(chat.getShootingLocationReceiverId())
	            .message(chat.getMessage())
	            .timeStamp(chat.getTimeStamp())
	            .build();


	}

}







