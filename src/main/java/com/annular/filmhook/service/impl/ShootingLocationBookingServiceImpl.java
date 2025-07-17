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
import com.annular.filmhook.util.NumberToWordsConverter;
import com.annular.filmhook.util.NumberToWordsConverter;
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
import java.net.URL;

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
    String paymentRetryLink = "https://film-hookapps.com/retry-payment";

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
	public ResponseEntity<?> sendShootingLocationBookingEmail(ShootingLocationBooking booking, ShootingLocationPayment payment, boolean isSuccess) {
	    try {
	        String txnid = payment.getTxnid();
	        String customerName = payment.getFirstname();
	        String customerEmail = payment.getEmail();
	        String locationName = booking.getProperty().getPropertyName();
	        String checkIn = booking.getShootStartDate().toString();
	        String checkOut = booking.getShootEndDate().toString();
	        String amount = "₹" + payment.getAmount();
	        String subject = isSuccess ? "Your Shooting Location Booking is Confirmed" : "Payment Failed - Booking Not Confirmed";

	        StringBuilder content = new StringBuilder();
	        content.append("<!DOCTYPE html><html><head>")
	            .append("<meta charset='UTF-8'>")
	            .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
	            .append("<style>")
	            .append("@media only screen and (max-width: 700px) {")
	            .append(".email-container { width: 100% !important; padding: 10px !important; }")
	            .append(".email-content td { display: block !important; width: 100% !important; box-sizing: border-box; }")
	            .append("img { max-width: 100% !important; height: auto !important; }")
	            .append("}")
	            .append("</style></head>")
	            .append("<body style='margin:0;padding:0;background:#f6f6f6;'>")

	            .append("<table cellpadding='0' cellspacing='0' width='100%' style='background:#f6f6f6;'>")
	            .append("<tr><td align='center'>")

	            .append("<table class='email-container' cellpadding='0' cellspacing='0' style='max-width:600px;width:100%;background:#ffffff;border-radius:8px;padding:0px;font-family:Arial,sans-serif;'>")

	            // Logo
	            .append("<tr class='email-content'><td align='center' style='padding-bottom:0px;'>")
	            .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/filmHookLogo.png' alt='FilmHook Logo' style='width:180px;max-width:100%;height:auto;'>")
	            .append("</td></tr>")

	            // Greeting & Status
	            .append("<tr><td style='color:#333;font-size:12px;line-height:1.6;'>")
	            .append("<p>Dear <strong>").append(customerName).append("</strong>,</p>")
	            .append("<p>").append(isSuccess
	                ? "We are excited to inform you that your shooting location booking has been successfully confirmed on <strong>Film-Hook Apps</strong>! 🎉"
	                : "We regret to inform you that your payment for booking the shooting location on <strong>Film-Hook Apps</strong> was unsuccessful. ❌")
	            .append("</p>")

	            // Booking Details
	            .append("<h3 style='color:#000;'>Booking Details:</h3>")
	            .append("<table width='100%' cellpadding='5' cellspacing='0' style='font-size:12px;'>")
	            .append("<tr><td><strong>Location:</strong></td><td>").append(locationName).append("</td></tr>")
	            .append("<tr><td><strong>Check-in:</strong></td><td>").append(checkIn).append("</td></tr>")
	            .append("<tr><td><strong>Check-out:</strong></td><td>").append(checkOut).append("</td></tr>")
	            .append("<tr><td><strong>Total Amount:</strong></td><td>").append(amount).append("</td></tr>")
	            .append("<tr><td><strong>Status:</strong></td><td>").append(isSuccess ? "Confirmed ✅" : "Failed ❌").append("</td></tr>")
	            .append("</table>");

	        // Retry or Success Message
	        if (!isSuccess) {
	            content.append("<p style='color:#d9534f;'>Unfortunately, due to the failed transaction, your booking has not been processed.</p>")
	                   .append("<p>🔄 <a href='").append(paymentRetryLink).append("' style='color:#007bff;'>Retry Payment</a></p>");
	        } else {
	            content.append("<p>📌 Your booking has been confirmed. Please check your profile or contact support if you need further information.</p>");
	        }

	        // Footer
	        content.append("<p style='margin-top:20px;'>If you need any assistance, feel free to contact our support team.</p>")
	            .append("<p style='margin-top:30px;'>Best Regards,<br><strong>Film-Hook Apps Team</strong><br>")
	            .append("<a href='mailto:support@film-hookapps.com'>📧 support@film-hookapps.com</a> | ")
	            .append("<a href='https://film-hookapps.com'>🌐 Visit Website</a></p>")

	            .append("<hr style='border:0;border-top:1px solid #ddd;margin:20px 0;'>")

	            // App Links
	            .append("<p>📲 Get the App:</p><p>")
	            .append("<a href='https://play.google.com/store/apps/details?id=com.projectfh&hl=en'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/PlayStore.jpeg' alt='Android' width='25'></a> ")
	            .append("<a href='#'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Apple.jpeg' alt='iOS' width='25'></a></p>")

	            // Social Media
	            .append("<p>📢 Follow Us:</p><p>")
	            .append("<a href='https://www.facebook.com/share/1BaDaYr3X6/?mibextid=qi2Omg'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/faceBook.jpeg' width='20'></a> ")
	            .append("<a href='https://x.com/Filmhook_Apps?t=KQJkjwuvBzTPOaL4FzDtIA&s=08/'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Twitter.jpeg' width='20'></a> ")
	            .append("<a href='https://www.threads.net/@filmhookapps/'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Threads.jpeg' width='20'></a> ")
	            .append("<a href='https://www.instagram.com/filmhookapps?igsh=dXdvNnB0ZGg5b2tx'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Instagram.jpeg' width='20'></a> ")
	            .append("<a href='https://youtube.com/@film-hookapps?si=oSz-bY4yt69TcThP'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Youtube.jpeg' width='20'></a> ")
	            .append("<a href='https://www.linkedin.com/in/film-hook-68666a353'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/linked.png' width='20'></a>")
	            .append("</p></td></tr></table></td></tr></table></body></html>");

	        // Send Email to Client
	        MimeMessage clientMessage = javaMailSender.createMimeMessage();
	        MimeMessageHelper clientHelper = new MimeMessageHelper(clientMessage, true);
	        clientHelper.setTo(customerEmail);
	        clientHelper.setSubject(subject);
	        clientHelper.setText(content.toString(), true);

	        if (isSuccess) {
	            byte[] invoiceBytes = generateInvoicePdf(payment, booking);
	            DataSource attachment = new ByteArrayDataSource(invoiceBytes, "application/pdf");
	            clientHelper.addAttachment("Invoice_" + txnid + ".pdf", attachment);
	        }

	        javaMailSender.send(clientMessage);

	        // === Owner Email ===
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

	                ownerContent.append("<!DOCTYPE html><html><head>")
	                    .append("<meta charset='UTF-8'>")
	                    .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
	                    .append("<style>")
	                    .append("@media only screen and (max-width: 600px) {")
	                    .append("  .email-container { width: 100% !important; padding: 10px !important; }")
	                    .append("  .email-content td { display: block !important; width: 100% !important; box-sizing: border-box; }")
	                    .append("  img { max-width: 100% !important; height: auto !important; }")
	                    .append("}")
	                    .append("</style></head><body style='margin:0;padding:0;background:#f6f6f6;'>")

	                    .append("<table cellpadding='0' cellspacing='0' width='100%' style='background-color:#f6f6f6;'>")
	                    .append("<tr><td align='center'>")
	                    .append("<table class='email-container' cellpadding='0' cellspacing='0' style='max-width:600px;width:100%;background:#ffffff;border-radius:8px;padding:10px;font-family:Arial,sans-serif;'>")

	                    // Logo
	                    .append("<tr><td align='center' style='padding-bottom:10px;'>")
	                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/filmHookLogo.png' alt='FilmHook Logo' style='width:160px;height:auto;'>")
	                    .append("</td></tr>")

	                    // Message
	                    .append("<tr><td style='color:#333;font-size:12px;'>")
	                    .append("<h3 style='color:#1a73e8;'> New Booking Alert</h3>")
	                    .append("<p>Dear <strong>").append(ownerName).append("</strong>,</p>")
	                    .append("<p>Your property <strong>").append(locationName).append("</strong> has been successfully booked by <strong>").append(customerName).append("</strong>.</p>")

	                    // Booking Details
	                    .append("<table width='100%' cellpadding='0' cellspacing='0' style='font-size:12px;'>")
	                    .append("<tr><td><strong>Check-in:</strong></td><td>").append(checkIn).append("</td></tr>")
	                    .append("<tr><td><strong>Check-out:</strong></td><td>").append(checkOut).append("</td></tr>")
	                    .append("<tr><td><strong>Client Email:</strong></td><td>").append(customerEmail).append("</td></tr>")
	                    .append("<tr><td><strong>Total Amount:</strong></td><td>").append(amount).append("</td></tr>")
	                    .append("</table>")

	                    .append("<p>📌 Please ensure your property is ready before check-in. Contact support if you need assistance.</p>")

	                    // Footer
	                    .append("<p style='margin-top:30px;'>Regards,<br><strong>FilmHook Team</strong><br>")
	                    .append("<a href='mailto:support@filmhook.com'>📧 support@filmhook.com</a><br>")
	                    .append("<a href='https://filmhook.com'>🌐 www.filmhook.com</a></p>")

	                    // App Links
	                    .append("<hr style='border:0;border-top:1px solid #ddd;margin:30px 0;'>")
	                    .append("<p>📲 Get the App:</p><p>")
	                    .append("<a href='https://play.google.com/store/apps/details?id=com.projectfh&hl=en'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/PlayStore.jpeg' alt='Android' width='30' style='margin-right:10px;'></a>")
	                    .append("<a href='#'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Apple.jpeg' alt='iOS' width='30'></a></p>")

	                    // Social Links
	                    .append("<p>📢 Follow Us:</p><p>")
	                    .append("<a href='https://www.facebook.com/share/1BaDaYr3X6/?mibextid=qi2Omg'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/faceBook.jpeg' width='25' style='margin-right:8px;'></a>")
	                    .append("<a href='https://x.com/Filmhook_Apps'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Twitter.jpeg' width='25' style='margin-right:8px;'></a>")
	                    .append("<a href='https://www.threads.net/@filmhookapps/'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Threads.jpeg' width='25' style='margin-right:8px;'></a>")
	                    .append("<a href='https://www.instagram.com/filmhookapps'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Instagram.jpeg' width='25' style='margin-right:8px;'></a>")
	                    .append("<a href='https://youtube.com/@film-hookapps'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Youtube.jpeg' width='30' style='margin-right:1px;'></a>")
	                    .append("<a href='https://www.linkedin.com/in/film-hook-68666a353'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/linked.png' width='35'></a>")
	                    .append("</p>")

	                    .append("</td></tr></table></td></tr></table></body></html>");
	                
	                

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
	        final int fontSize = 9;

	        int days = (int) ChronoUnit.DAYS.between(booking.getShootStartDate(), booking.getShootEndDate()) + 1;
	        double rate = booking.getPricePerDay() != null ? booking.getPricePerDay() : 0.0;
	        double base = booking.getBaseAmount() != null ? booking.getBaseAmount() : 0.0;
	        double gst = booking.getGstAmount() != null ? booking.getGstAmount() : 0.0;
	        double total = booking.getTotalAmount() != null ? booking.getTotalAmount() : base + gst;

	        // Logo
	        InputStream logoStream = new URL("https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/filmHookLogo.png").openStream();
	        Image logo = new Image(ImageDataFactory.create(logoStream.readAllBytes()))
	                .scaleToFit(120, 60)
	                .setHorizontalAlignment(HorizontalAlignment.CENTER);
	        doc.add(logo);

	        doc.add(new Paragraph("TAX INVOICE")
	                .setTextAlignment(TextAlignment.CENTER)
	                .setFontSize(14)
	                .setBold()
	                .setFontColor(blue)
	                .setMarginBottom(10));

	        // Invoice metadata
	        Table meta = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
	                .setWidth(UnitValue.createPercentValue(100))
	                .setMarginTop(10);

	        meta.addCell(getPlainCell("Invoice To: " + payment.getFirstname()));
	        meta.addCell(getPlainCell("Issued by: FilmHook Pvt. Ltd.\nGSTIN: 29ABCDE1234F2Z5\nAddress: Bangalore\nPhone: +91-9876543210"));

	        meta.addCell(getPlainCell("Order ID: " + payment.getTxnid()));
	        meta.addCell(getPlainCell("Date of Invoice: " + LocalDate.now()));

	        meta.addCell(getPlainCell("Service Description: Shooting Location Rental"));
	        meta.addCell(getPlainCell("HSN Code: 996331\nReverse Charges: No"));

	        doc.add(meta);

	        // Item Table
	        Table itemTable = new Table(UnitValue.createPercentArray(new float[]{25, 10, 10, 15, 15, 10, 15}))
	                .setWidth(UnitValue.createPercentValue(100))
	                .setMarginTop(10);

	        itemTable.addHeaderCell(getStyledBottomBorderHeader("Description"));
	        itemTable.addHeaderCell(getStyledBottomBorderHeader("UOM"));
	        itemTable.addHeaderCell(getStyledBottomBorderHeader("Qty"));
	        itemTable.addHeaderCell(getStyledBottomBorderHeader("Unit Price"));
	        itemTable.addHeaderCell(getStyledBottomBorderHeader("Amount"));
	        itemTable.addHeaderCell(getStyledBottomBorderHeader("Discount"));
	        itemTable.addHeaderCell(getStyledBottomBorderHeader("Net Value"));

	        itemTable.addCell(getStyledBottomBorderCell("Shooting Location: " + booking.getProperty().getPropertyName()));
	        itemTable.addCell(getStyledBottomBorderCell("OTH"));
	        itemTable.addCell(getStyledBottomBorderCell(String.valueOf(days)));
	        itemTable.addCell(getStyledBottomBorderCell("₹ " + String.format("%.2f", rate)));
	        itemTable.addCell(getStyledBottomBorderCell("₹ " + String.format("%.2f", rate * days)));
	        itemTable.addCell(getStyledBottomBorderCell("₹ 0.00"));
	        itemTable.addCell(getStyledBottomBorderCell("₹ " + String.format("%.2f", base)));

	        doc.add(itemTable);

	        // Taxes
	        Table taxTable = new Table(UnitValue.createPercentArray(new float[]{60, 10, 30}))
	                .setWidth(UnitValue.createPercentValue(100))
	                .setMarginTop(10);

	        taxTable.addCell(getLightCell("Taxes"));
	        taxTable.addCell(getLightCell("Rate"));
	        taxTable.addCell(getLightCell("Amount"));

	        taxTable.addCell(getPlainCell("CGST")).addCell(getPlainCell("2.5%")).addCell(getPlainCell("₹ " + String.format("%.2f", gst / 2)));
	        taxTable.addCell(getPlainCell("SGST")).addCell(getPlainCell("2.5%")).addCell(getPlainCell("₹ " + String.format("%.2f", gst / 2)));

	        taxTable.addCell(getLightCell("Total Taxes")).addCell(getPlainCell("")).addCell(getPlainCell("₹ " + String.format("%.2f", gst)));

	        doc.add(taxTable);

	        // Total
	        Table totalTable = new Table(UnitValue.createPercentArray(new float[]{70, 30}))
	                .setWidth(UnitValue.createPercentValue(100))
	                .setMarginTop(10);

	        totalTable.addCell(new Cell().add(new Paragraph("Invoice Total").setFontSize(11).setBold())
	                .setBorder(Border.NO_BORDER));

	        totalTable.addCell(new Cell().add(new Paragraph("₹ " + String.format("%.2f", total))
	                        .setFontColor(blue)
	                        .setBold()
	                        .setFontSize(11)
	                        .setTextAlignment(TextAlignment.RIGHT))
	                .setBorder(Border.NO_BORDER));

	        doc.add(totalTable);

	        // Amount in words
	        doc.add(new Paragraph("\nInvoice total in words: " + convertToIndianCurrency(total))
	                .setFontSize(9)
	                .setItalic());

	        // Declaration
	        doc.add(new Paragraph("\nDeclaration").setBold().setFontSize(11).setMarginTop(20));
	        doc.add(new Paragraph("We declare that this invoice shows the actual price of the services provided and that all particulars are true and correct.")
	                .setFontSize(fontSize));

	        // Signature
	        InputStream signStream = new URL("https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/filmHookLogo.png").openStream();
	        Image sign = new Image(ImageDataFactory.create(signStream.readAllBytes())).scaleToFit(80, 30);
	        Paragraph signText = new Paragraph("Digitally Signed by FilmHook Pvt. Ltd.")
	                .setFontSize(9)
	                .setTextAlignment(TextAlignment.RIGHT);
	        Paragraph signBlock = new Paragraph().add(sign).add("\n").add(signText);
	        Table signTable = new Table(1).setWidth(UnitValue.createPercentValue(100)).setMarginTop(30);
	        signTable.addCell(new Cell().add(signBlock).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
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
	            .add(new Paragraph(text).setBold().setFontSize(9))
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
	public static String convertToIndianCurrency(double amount) {
	    long rupees = (long) amount;
	    long paise = Math.round((amount - rupees) * 100);
	    return NumberToWordsConverter.convert(rupees) + " Rupees " +
	           (paise > 0 ? NumberToWordsConverter.convert(paise) + " Paise" : "") + " Only";
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







