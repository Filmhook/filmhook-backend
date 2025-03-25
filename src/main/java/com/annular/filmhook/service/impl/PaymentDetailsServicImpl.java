package com.annular.filmhook.service.impl;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.PaymentDetails;
import com.annular.filmhook.model.Promote;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.PaymentDetailsRepository;
import com.annular.filmhook.repository.PromoteRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.PaymentDetailsService;
import com.annular.filmhook.util.HashGenerator;
import com.annular.filmhook.webmodel.PaymentDetailsWebModel;

@Service
public class PaymentDetailsServicImpl implements PaymentDetailsService{
	

	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private PromoteRepository promoteRepository; 

	@Autowired
    private PaymentDetailsRepository paymentDetailsRepository;
	
	@Autowired
	UserRepository userRepository;

    private final String key = "oXregF";
    private final String salt = "fGiczQ8QDLit7B5iEHGQ2glKXv4wKPqe";

    @Override
    public ResponseEntity<?> savePayment(PaymentDetailsWebModel webModel) {
        String hash = HashGenerator.generateHash(
                key,
                webModel.getTxnid(),
                webModel.getAmount(),
                webModel.getProductinfo(),
                webModel.getFirstname(),
                webModel.getEmail(),
                salt
        );

        PaymentDetails details = PaymentDetails.builder()
                .txnid(webModel.getTxnid())
                .amount(webModel.getAmount())
                .productinfo(webModel.getProductinfo())
                .firstname(webModel.getFirstname())
                .promoteId(webModel.getPromoteId())
                .email(webModel.getEmail())
                .userId(webModel.getUserId())
                .postId(webModel.getPostId())
                .paymentHash(hash)
                .paymentkey("oXregF")
                .salt("fGiczQ8QDLit7B5iEHGQ2glKXv4wKPqe")
                .build();

        paymentDetailsRepository.save(details);

        return ResponseEntity.ok(new Response(1,"success",details));
    }
    
    @Override
    public ResponseEntity<?> emailSend(PaymentDetailsWebModel paymentDetailsWebModel) {
    	try {
            // Fetch user details
    		Integer userId = paymentDetailsWebModel.getUserId();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Integer promoteId = paymentDetailsWebModel.getPromoteId();
            Promote promoteData = promoteRepository.findById(promoteId)
                    .orElseThrow(() -> new RuntimeException("promote not found"));

            String to = user.getEmail();
            String subject = "🎉 Congratulations! Your Post is Successfully Promoted";

            String content = "<p>Dear " + user.getName() + ",</p>"
                    + "<p>We are excited to inform you that your promoted post has been successfully activated on <strong>Film-Hook Apps</strong>! 🚀</p>"
                    + "<h3>Promotion Details:</h3>"
                    + "<ul>"
                    + "<li><strong>Company Name:</strong> Film-Hook Apps</li>"
                    + "<li><strong>Amount Paid:</strong> ₹" + promoteData.getAmount() + "</li>"
                    + "<li><strong>Validity:</strong> " + promoteData.getNumberOfDays() + " Days</li>"
                  //  + "<li><strong>Estimated Reach:</strong> " + promoteData.getEstimatedReach() + " Users</li>"
                    + "</ul>"
                    + "<p>Your post is now set to reach a wider audience, increasing visibility and engagement. Thank you for trusting <strong>Film-Hook Apps</strong> for your promotion needs!</p>"
                    + "<p>If you have any questions or need further assistance, feel free to contact our support team.</p>"
                    + "<br><p>Best Regards,<br>Film-Hook Apps Team<br>📧 support@filmhook.com | 🌐 www.filmhook.com</p>";

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            javaMailSender.send(message);
            return ResponseEntity.ok("Promotion email sent successfully to userId: " + userId);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + e.getMessage());
        }
    }
    @Override
    public ResponseEntity<?> promotionPending(PaymentDetailsWebModel paymentDetailsWebModel) {
        try {
            // Fetch user details
            Integer userId = paymentDetailsWebModel.getUserId();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Integer promoteId = paymentDetailsWebModel.getPromoteId();
            Promote promoteData = promoteRepository.findById(promoteId)
                    .orElseThrow(() -> new RuntimeException("Promote data not found"));

            // Extract promotion details
            String to = user.getEmail();
            String subject = "⏳ Your Post Promotion is Pending";

            String amountPaid = String.valueOf(paymentDetailsWebModel.getAmount());
            String reason = paymentDetailsWebModel.getReason() != null ? paymentDetailsWebModel.getReason() : "Under review";

            // HTML email content
            String content = "<html><body>" +
                    "<p>Dear " + user.getName() + ",</p>" +
                    "<p>We wanted to inform you that your post promotion request on <b>Film-Hook Apps</b> is currently in <b>pending status</b>.</p>" +
                    "<p><b>Promotion Details:</b></p>" +
                    "<ul>" +
                    "<li><b>Company Name:</b> Film-Hook Apps</li>" +
                    "<li><b>Amount Paid:</b> ₹" + amountPaid + "</li>" +
                    "<li><b>Status:</b> Pending</li>" +
                    "<li><b>Reason:</b> " + reason + "</li>" +
                    "</ul>" +
                    "<p>We are processing your request and will notify you once your promotion is activated.</p>" +
                    "<p>If any action is required from your end, we will reach out to you shortly.</p>" +
                    "<p>If you have any questions, feel free to contact our support team.</p>" +
                    "<p>Best Regards,</p>" +
                    "<p><b>Film-Hook Apps Team</b></p>" +
                    "<p>📧 <a href='mailto:support@film-hookapps.com'>support@film-hookapps.com</a> | 🌐 <a href='https://film-hookapps.com/'>Visit Website</a></p>" +
                    "<p>📲 Get the App:</p>" +
                    "<p><a href='#'><img src='android_icon.png' alt='Android' width='30'></a> " +
                    "<a href='#'><img src='ios_icon.png' alt='iOS' width='30'></a></p>" +
                    "<p>📢 Follow Us:</p>" +
                    "<p><a href='#'><img src='facebook_icon.png' width='30'></a> " +
                    "<a href='#'><img src='twitter_icon.png' width='30'></a> " +
                    "<a href='#'><img src='instagram_icon.png' width='30'></a> " +
                    "<a href='#'><img src='youtube_icon.png' width='30'></a></p>" +
                    "</body></html>";

            // Send email
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            javaMailSender.send(message);

            return ResponseEntity.ok("Promotion email sent successfully to userId: " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> promotionFailed(PaymentDetailsWebModel paymentDetailsWebModel) {
        try {
            // Fetch user details
            Integer userId = paymentDetailsWebModel.getUserId();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Extract payment details
            String to = user.getEmail();
            String subject = "⚠️ Payment Failed for Your Post Promotion";
            String amountAttempted = String.valueOf(paymentDetailsWebModel.getAmount());
            String paymentRetryLink = "https://film-hookapps.com/retry-payment"; // Replace with actual retry link

            // HTML email content
            String content = "<html><body>" +
                    "<p>Dear " + user.getName() + ",</p>" +
                    "<p>We regret to inform you that your payment for promoting your post on <b>Film-Hook Apps</b> was unsuccessful. ❌</p>" +
                    "<p><b>Payment Details:</b></p>" +
                    "<ul>" +
                    "<li><b>Company Name:</b> Film-Hook Apps</li>" +
                    "<li><b>Amount Attempted:</b> ₹" + amountAttempted + "</li>" +
                    "<li><b>Status:</b> Payment Failed</li>" +
                    "</ul>" +
                    "<p>Unfortunately, due to this failure, your post promotion has not been activated. Please try again using a different payment method or ensure that your payment details are correct.</p>" +
                    "<p>🔄 <a href='" + paymentRetryLink + "'><b>Retry Payment</b></a></p>" +
                    "<p>If you need any assistance, feel free to contact our support team. We're happy to help!</p>" +
                    "<p>Best Regards,</p>" +
                    "<p><b>Film-Hook Apps Team</b></p>" +
                    "<p>📧 <a href='mailto:support@film-hookapps.com'>support@film-hookapps.com</a> | 🌐 <a href='https://film-hookapps.com/'>Visit Website</a></p>" +
                    "<p>📲 Get the App:</p>" +
                    "<p><a href='#'><img src='android_icon.png' alt='Android' width='30'></a> " +
                    "<a href='#'><img src='ios_icon.png' alt='iOS' width='30'></a></p>" +
                    "<p>📢 Follow Us:</p>" +
                    "<p><a href='#'><img src='facebook_icon.png' width='30'></a> " +
                    "<a href='#'><img src='twitter_icon.png' width='30'></a> " +
                    "<a href='#'><img src='instagram_icon.png' width='30'></a> " +
                    "<a href='#'><img src='youtube_icon.png' width='30'></a></p>" +
                    "</body></html>";

            // Send email
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            javaMailSender.send(message);

            return ResponseEntity.ok("Payment failure email sent successfully to userId: " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> promotionForCron(PaymentDetailsWebModel paymentDetailsWebModel) {
        try {
            // Fetch user details
            Integer userId = paymentDetailsWebModel.getUserId();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Fetch promotion details
            Integer promoteId = paymentDetailsWebModel.getPromoteId();
            Promote promoteData = promoteRepository.findById(promoteId)
                    .orElseThrow(() -> new RuntimeException("Promote data not found"));

            String to = user.getEmail();
            String subject = "⏳ Reminder: Your Post Promotion Ends in 24 Hours";
            String amountPaid = String.valueOf(paymentDetailsWebModel.getAmount());
//            String validity = promoteData.getValidity() + " Days";
//            String expirationDate = promoteData.getExpirationDate().toString(); // Ensure correct format
            String renewalLink = "https://film-hookapps.com/renew-promotion"; // Replace with actual link

            // HTML email content
            String content = "<html><body>" +
                    "<p>Dear " + user.getName() + ",</p>" +
                    "<p>We hope you're enjoying the benefits of your promoted post on <b>Film-Hook Apps</b>! 🚀</p>" +
                    "<p>This is a friendly reminder that your promotion will expire in <b>24 hours</b>.</p>" +
                    "<p><b>Promotion Details:</b></p>" +
                    "<ul>" +
                    "<li><b>Company Name:</b> Film-Hook Apps</li>" +
                    "<li><b>Amount Paid:</b> ₹" + amountPaid + "</li>" +
//                    "<li><b>Validity:</b> " + validity + "</li>" +
//                    "<li><b>Expiration Date & Time:</b> " + expirationDate + "</li>" +
                    "</ul>" +
                    "<p>To continue reaching more users, you can extend your promotion before it ends.</p>" +
                    "<p>🔄 <a href='" + renewalLink + "'><b>Renew Promotion</b></a></p>" +
                    "<p>If you have any questions or need assistance, feel free to contact our support team.</p>" +
                    "<p>Best Regards,</p>" +
                    "<p><b>Film-Hook Apps Team</b></p>" +
                    "<p>📧 <a href='mailto:support@film-hookapps.com'>support@film-hookapps.com</a> | 🌐 <a href='https://film-hookapps.com/'>Visit Website</a></p>" +
                    "<p>📲 Get the App:</p>" +
                    "<p><a href='#'><img src='android_icon.png' alt='Android' width='30'></a> " +
                    "<a href='#'><img src='ios_icon.png' alt='iOS' width='30'></a></p>" +
                    "<p>📢 Follow Us:</p>" +
                    "<p><a href='#'><img src='facebook_icon.png' width='30'></a> " +
                    "<a href='#'><img src='twitter_icon.png' width='30'></a> " +
                    "<a href='#'><img src='instagram_icon.png' width='30'></a> " +
                    "<a href='#'><img src='youtube_icon.png' width='30'></a></p>" +
                    "</body></html>";

            // Send email
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            javaMailSender.send(message);

            return ResponseEntity.ok("Promotion expiry reminder email sent successfully to userId: " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + e.getMessage());
        }
    }


}
