package com.annular.filmhook.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.annular.filmhook.Response;
import com.annular.filmhook.controller.PaymentDetailsController;
import com.annular.filmhook.model.InAppNotification;
import com.annular.filmhook.model.PaymentDetails;
import com.annular.filmhook.model.Posts;
import com.annular.filmhook.model.Promote;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.InAppNotificationRepository;
import com.annular.filmhook.repository.PaymentDetailsRepository;
import com.annular.filmhook.repository.PostsRepository;
import com.annular.filmhook.repository.PromoteRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.PaymentDetailsService;
import com.annular.filmhook.util.HashGenerator;
import com.annular.filmhook.webmodel.PaymentDetailsWebModel;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class PaymentDetailsServicImpl implements PaymentDetailsService{
	   @Autowired
	    private InAppNotificationRepository inAppNotificationRepository;
	   @Autowired
	   PostsRepository postsRepository;
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private PromoteRepository promoteRepository; 

	@Autowired
    private PaymentDetailsRepository paymentDetailsRepository;
	
	@Autowired
	UserRepository userRepository;
	public static final Logger logger = LoggerFactory.getLogger(PaymentDetailsController.class);

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
                .promotionStatus("PENDING")
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
            Integer paymentId = paymentDetailsWebModel.getPaymentId();
            
         // Step 2: Fetch promotion details using paymentId
            PaymentDetails promote = paymentDetailsRepository.findByPaymentId(paymentId)
                    .orElseThrow(() -> new RuntimeException("Promotion not found for payment ID: " + paymentId));

            // Step 3: Update promotion status
            promote.setPromotionStatus("SUCCESS"); 
            paymentDetailsRepository.save(promote);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            Integer promoteId = paymentDetailsWebModel.getPromoteId();
            Promote promoteData = promoteRepository.findById(promoteId)
                    .orElseThrow(() -> new RuntimeException("Promote data not found with ID: " + promoteId));

            String to = user.getEmail();
            String subject = "🎉 Congratulations! Your Post is Successfully Promoted";

            // Building email content using StringBuilder
            StringBuilder content = new StringBuilder();

            content.append("<html><body>")
                    .append("<div style='text-align: center;'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/filmHookLogo.png' width='200' alt='FilmHook Logo'>")
                    .append("</div>")
                    .append("<p>Dear ").append(user.getName()).append(",</p>")
                    .append("<p>We are excited to inform you that your promoted post has been successfully activated on <strong>Film-Hook Apps</strong>! 🚀</p>")
                    .append("<h3>Promotion Details:</h3>")
                    .append("<ul>")
                    .append("<li><strong>Company Name:</strong> Film-Hook Apps</li>")
                    .append("<li><strong>Amount Paid:</strong> ₹").append(promoteData.getAmount()).append("</li>")
                    .append("<li><strong>Validity:</strong> ").append(promoteData.getNumberOfDays()).append(" Days</li>")
                    .append("</ul>")
                    .append("<p>Your post is now set to reach a wider audience, increasing visibility and engagement. Thank you for trusting <strong>Film-Hook Apps</strong> for your promotion needs!</p>")
                    .append("<p>If you have any questions or need further assistance, feel free to contact our support team.</p>")
                    .append("<br><p>Best Regards,</p>")
                    .append("<p><b>Film-Hook Apps Team</b></p>")
                    .append("<p>📧 <a href='mailto:support@film-hookapps.com'>support@film-hookapps.com</a> | 🌐 <a href='https://film-hookapps.com/'>Visit Website</a></p>")
                    .append("<p>📲 Get the App:</p>")
                    .append("<p><a href='https://play.google.com/store/apps/details?id=com.projectfh&hl=en'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/PlayStore.jpeg' alt='Android' width='30'></a> ")
                    .append("<a href='#'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Apple.jpeg' alt='iOS' width='30'></a></p>")
                    .append("<p>📢 Follow Us:</p>")
                    .append("<p>")
                    .append("<a href='https://www.facebook.com/share/1BaDaYr3X6/?mibextid=qi2Omg' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/faceBook.jpeg' width='30'></a> ")
                    .append("<a href='https://x.com/Filmhook_Apps?t=KQJkjwuvBzTPOaL4FzDtIA&s=08/' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Twitter.jpeg' width='30'></a> ")
                    .append("<a href='https://www.threads.net/@filmhookapps/' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Threads.jpeg' width='30'></a> ")
                    .append("<a href='https://www.instagram.com/filmhookapps?igsh=dXdvNnB0ZGg5b2tx' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Instagram.jpeg' width='30'></a> ")
                    .append("<a href='https://youtube.com/@film-hookapps?si=oSz-bY4yt69TcThP' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Youtube.jpeg' width='30'></a>")
                    .append("<a href='https://www.linkedin.com/in/film-hook-68666a353' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/linedIn.jpeg' width='30'></a>")
                    .append("</p>")
                    .append("</body></html>");

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content.toString(), true);
            
			Posts promotedPost = postsRepository.findById(promoteData.getPostId())
                    .orElseThrow(() -> new RuntimeException("Post not found with ID: " + promoteData.getPostId()));

            promotedPost.setStatus(true); 
            promotedPost.setPromoteFlag(true);
            promotedPost.setPromoteStatus(true);
            postsRepository.save(promotedPost);

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
            Integer paymentId = paymentDetailsWebModel.getPaymentId();
            
         // Step 2: Fetch promotion details using paymentId
            PaymentDetails promote = paymentDetailsRepository.findByPaymentId(paymentId)
                    .orElseThrow(() -> new RuntimeException("Promotion not found for payment ID: " + paymentId));

            // Step 3: Update promotion status
            promote.setPromotionStatus("PENDING"); // or "ACTIVE", "SUCCESS" etc. depending on your logic
            paymentDetailsRepository.save(promote);

            Integer promoteId = paymentDetailsWebModel.getPromoteId();
            Promote promoteData = promoteRepository.findById(promoteId)
                    .orElseThrow(() -> new RuntimeException("Promote data not found with ID: " + promoteId));

            // Extract promotion details
            String to = user.getEmail();
            String subject = "⏳ Your Post Promotion is Pending";

            String amountPaid = String.valueOf(paymentDetailsWebModel.getAmount());
            String reason = paymentDetailsWebModel.getReason() != null ? paymentDetailsWebModel.getReason() : "Under review";

            // Use StringBuilder to construct email content
            StringBuilder content = new StringBuilder();
            content.append("<html><body>")
                    .append("<div style='text-align: center;'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/filmHookLogo.png' width='200' alt='FilmHook Logo'>")
                    .append("</div>")
                    .append("<p>Dear ").append(user.getName()).append(",</p>")
                    .append("<p>We are excited to inform you that your promoted post has been successfully activated on <strong>Film-Hook Apps</strong>! 🚀</p>")
                    .append("<h3>Promotion Details:</h3>")
                    .append("<ul>")
                    .append("<li><b>Company Name:</b> Film-Hook Apps</li>")
                    .append("<li><strong>Amount Paid:</strong> ₹").append(promoteData.getAmount()).append("</li>")
                    .append("<li><b>Status:</b> Pending</li>")
                    .append("<li><b>Reason:</b> ").append(reason).append("</li>")
                    .append("</ul>")
                    .append("<p>We are processing your request and will notify you once your promotion is activated.</p>")
                    .append("<p>If any action is required from your end, we will reach out to you shortly.</p>")
                    .append("<p>If you have any questions, feel free to contact our support team.</p>")
                    .append("<br><p>Best Regards,</p>")
                    .append("<p><b>Film-Hook Apps Team</b></p>")
                    .append("<p>📧 <a href='mailto:support@film-hookapps.com'>support@film-hookapps.com</a> | 🌐 <a href='https://film-hookapps.com/'>Visit Website</a></p>")
                    .append("<p>📲 Get the App:</p>")
                    .append("<p><a href='https://play.google.com/store/apps/details?id=com.projectfh&hl=en'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/PlayStore.jpeg' alt='Android' width='30'></a> ")
                    .append("<a href='#'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Apple.jpeg' alt='iOS' width='30'></a></p>")
                    .append("<p>📢 Follow Us:</p>")
                    .append("<p>")
                    .append("<a href='https://www.facebook.com/share/1BaDaYr3X6/?mibextid=qi2Omg' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/faceBook.jpeg' width='30'></a> ")
                    .append("<a href='https://x.com/Filmhook_Apps?t=KQJkjwuvBzTPOaL4FzDtIA&s=08/' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Twitter.jpeg' width='30'></a> ")
                    .append("<a href='https://www.threads.net/@filmhookapps/' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Threads.jpeg' width='30'></a> ")
                    .append("<a href='https://www.instagram.com/filmhookapps?igsh=dXdvNnB0ZGg5b2tx' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Instagram.jpeg' width='30'></a> ")
                    .append("<a href='https://youtube.com/@film-hookapps?si=oSz-bY4yt69TcThP' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Youtube.jpeg' width='30'></a>")
                    .append("<a href='https://www.linkedin.com/in/film-hook-68666a353' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/linedIn.jpeg' width='30'></a>")
                    .append("</p>")
                    .append("</body></html>");

            // Send email
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content.toString(), true); // Ensure content is converted to a string

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
            Integer paymentId = paymentDetailsWebModel.getPaymentId();
            
            // Step 2: Fetch promotion details using paymentId
               PaymentDetails promote = paymentDetailsRepository.findByPaymentId(paymentId)
                       .orElseThrow(() -> new RuntimeException("Promotion not found for payment ID: " + paymentId));

               // Step 3: Update promotion status
               promote.setPromotionStatus("FAILED"); // or "ACTIVE", "SUCCESS" etc. depending on your logic
               paymentDetailsRepository.save(promote);


            Integer promoteId = paymentDetailsWebModel.getPromoteId();
            Promote promoteData = promoteRepository.findById(promoteId)
                    .orElseThrow(() -> new RuntimeException("Promote data not found with ID: " + promoteId));

            // Extract payment details
            String to = user.getEmail();
            String subject = "⚠️ Payment Failed for Your Post Promotion";
            String amountAttemptedString = String.valueOf(paymentDetailsWebModel.getAmount());
            String paymentRetryLink = "https://film-hookapps.com/retry-payment?txnid=" + promoteData.getPromoteId();


            // Use StringBuilder to construct email content
            StringBuilder content = new StringBuilder();
            content.append("<html><body>")
                    .append("<div style='text-align: center;'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/filmHookLogo.png' width='200' alt='FilmHook Logo'>")
                    .append("</div>")
                    .append("<p>Dear ").append(user.getName()).append(",</p>")
                    .append("<p>We regret to inform you that your payment for promoting your post on <b>Film-Hook Apps</b> was unsuccessful. ❌</p>")
                    .append("<p><b>Payment Details:</b></p>")
                    .append("<ul>")
                    .append("<li><b>Company Name:</b> Film-Hook Apps</li>")
                    .append("<li><strong>Amount Paid:</strong> ₹").append(promoteData.getAmount()).append("</li>")
                    .append("<li><b>Status:</b> Payment Failed</li>")
                    .append("</ul>")
                    .append("<p>Unfortunately, due to this failure, your post promotion has not been activated. Please try again using a different payment method or ensure that your payment details are correct.</p>")
                    .append("<p>🔄 <a href='").append(paymentRetryLink).append("'><b>Retry Payment</b></a></p>")
                    .append("<p>If you need any assistance, feel free to contact our support team. We're happy to help!</p>")
                    .append("<br><p>Best Regards,</p>")
                    .append("<p><b>Film-Hook Apps Team</b></p>")
                    .append("<p>📧 <a href='mailto:support@film-hookapps.com'>support@film-hookapps.com</a> | 🌐 <a href='https://film-hookapps.com/'>Visit Website</a></p>")
                    .append("<p>📲 Get the App:</p>")
                    .append("<p><a href='https://play.google.com/store/apps/details?id=com.projectfh&hl=en'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/PlayStore.jpeg' alt='Android' width='30'></a> ")
                    .append("<a href='#'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Apple.jpeg' alt='iOS' width='30'></a></p>")
                    .append("<p>📢 Follow Us:</p>")
                    .append("<p>")
                    .append("<a href='https://www.facebook.com/share/1BaDaYr3X6/?mibextid=qi2Omg' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/faceBook.jpeg' width='30'></a> ")
                    .append("<a href='https://x.com/Filmhook_Apps?t=KQJkjwuvBzTPOaL4FzDtIA&s=08/' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Twitter.jpeg' width='30'></a> ")
                    .append("<a href='https://www.threads.net/@filmhookapps/' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Threads.jpeg' width='30'></a> ")
                    .append("<a href='https://www.instagram.com/filmhookapps?igsh=dXdvNnB0ZGg5b2tx' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Instagram.jpeg' width='30'></a> ")
                    .append("<a href='https://youtube.com/@film-hookapps?si=oSz-bY4yt69TcThP' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Youtube.jpeg' width='30'></a>")
                    .append("<a href='https://www.linkedin.com/in/film-hook-68666a353' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/linked.png' width='30'></a>")
                    .append("</p>")
                    .append("</body></html>");

            // Send email
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content.toString(), true);

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
            Integer paymentId = paymentDetailsWebModel.getPaymentId();
            
            // Step 2: Fetch promotion details using paymentId
               PaymentDetails promote = paymentDetailsRepository.findByPaymentId(paymentId)
                       .orElseThrow(() -> new RuntimeException("Promotion not found for payment ID: " + paymentId));

               // Step 3: Update promotion status
               promote.setPromotionStatus("EXPIRE"); // or "ACTIVE", "SUCCESS" etc. depending on your logic
               paymentDetailsRepository.save(promote);


            // Fetch promotion details
            Integer promoteId = paymentDetailsWebModel.getPromoteId();
            Promote promoteData = promoteRepository.findById(promoteId)
                    .orElseThrow(() -> new RuntimeException("Promote data not found"));

            String to = user.getEmail();
            String subject = "⏳ Reminder: Your Post Promotion Ends in 24 Hours";
            String amountPaid = String.valueOf(paymentDetailsWebModel.getAmount());
            String renewalLink = "https://film-hookapps.com/renew-promotion"; // Replace with actual link

            // Use StringBuilder to construct email content
            StringBuilder content = new StringBuilder();
            content.append("<html><body>")
                    .append("<div style='text-align: center;'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/filmHookLogo.png' width='200' alt='FilmHook Logo'>")
                    .append("</div>")
                    .append("<p>Dear ").append(user.getName()).append(",</p>")
                    .append("<p>We hope you're enjoying the benefits of your promoted post on <b>Film-Hook Apps</b>! 🚀</p>")
                    .append("<p>This is a friendly reminder that your promotion will expire in <b>24 hours</b>.</p>")
                    .append("<p><b>Promotion Details:</b></p>")
                    .append("<ul>")
                    .append("<li><b>Company Name:</b> Film-Hook Apps</li>")
                    .append("<li><strong>Amount Paid:</strong> ₹").append(promoteData.getAmount()).append("</li>")
                    .append("</ul>")
                    .append("<p>To continue reaching more users, you can extend your promotion before it ends.</p>")
                    .append("<p>🔄 <a href='").append(renewalLink).append("'><b>Renew Promotion</b></a></p>")
                    .append("<p>If you have any questions or need assistance, feel free to contact our support team.</p>")
                    .append("<p><b>Film-Hook Apps Team</b></p>")
                    .append("<p>📧 <a href='mailto:support@film-hookapps.com'>support@film-hookapps.com</a> | 🌐 <a href='https://film-hookapps.com/'>Visit Website</a></p>")
                    .append("<p>📲 Get the App:</p>")
                    .append("<p><a href='https://play.google.com/store/apps/details?id=com.projectfh&hl=en'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/PlayStore.jpeg' alt='Android' width='30'></a> ")
                    .append("<a href='#'><img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Apple.jpeg' alt='iOS' width='30'></a></p>")
                    .append("<p>📢 Follow Us:</p>")
                    .append("<p>")
                    .append("<a href='https://www.facebook.com/share/1BaDaYr3X6/?mibextid=qi2Omg' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/faceBook.jpeg' width='30'></a> ")
                    .append("<a href='https://x.com/Filmhook_Apps?t=KQJkjwuvBzTPOaL4FzDtIA&s=08/' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Twitter.jpeg' width='30'></a> ")
                    .append("<a href='https://www.threads.net/@filmhookapps/' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Threads.jpeg' width='30'></a> ")
                    .append("<a href='https://www.instagram.com/filmhookapps?igsh=dXdvNnB0ZGg5b2tx' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Instagram.jpeg' width='30'></a> ")
                    .append("<a href='https://youtube.com/@film-hookapps?si=oSz-bY4yt69TcThP' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Youtube.jpeg' width='30'></a>")
                    .append("<a href='https://www.linkedin.com/in/film-hook-68666a353' target='_blank'>")
                    .append("<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/linedIn.jpeg' width='30'></a>")
                    .append("</p>")
                    .append("</body></html>");

            // Send email
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content.toString(), true);

            javaMailSender.send(message);
            
         // Send in-app and push notification
            String notificationTitle = "Promotion Expiring Soon!";
            String notificationMessage = "Hi " + user.getName() + ", your promotion will expire in 24 hours. Renew now to continue.";

            InAppNotification notification = InAppNotification.builder()
                    .senderId(0) // 0 or null for system/admin
                    .receiverId(user.getUserId())
                    .title(notificationTitle)
                    .message(notificationMessage)
                    .userType("PROMOTION_EXPIRY")
                    .id(promoteId)
                    .postId(String.valueOf(promoteData.getPostId()))
                    .isRead(false)
                    .isDeleted(false)
                    .createdOn(new Date())
                    .createdBy(0)
                    .build();

            inAppNotificationRepository.save(notification);

            // Push notification
            String deviceToken = user.getFirebaseDeviceToken();
            if (deviceToken != null && !deviceToken.trim().isEmpty()) {
                try {
                	 // FCM Notification
	                Notification notificationData = Notification.builder()
	                        .setTitle(notificationTitle)
	                        .setBody(notificationMessage)
	                        .build();

	                // Android Config
	                AndroidNotification androidNotification = AndroidNotification.builder()
	                        .setIcon("ic_notification")
	                        .setColor("#00A2E8")
	                        .build();

	                AndroidConfig androidConfig = AndroidConfig.builder()
	                        .setNotification(androidNotification)
	                        .build();
                    Message firebaseMessage = Message.builder()
                            .setNotification(notificationData)
                            .setAndroidConfig(androidConfig)
                            .putData("type", "PROMOTION_EXPIRY")
                            .putData("paymentId", String.valueOf(paymentId))
                            .putData("postId", String.valueOf(promoteData.getPostId()))
                            .setToken(deviceToken)
                            .build();

                    String response = FirebaseMessaging.getInstance().send(firebaseMessage);
                    logger.info("Promotion expiry push notification sent: " + response);
                } catch (FirebaseMessagingException e) {
                    logger.error("Error sending push notification for promotion expiry", e);
                }
            }


            return ResponseEntity.ok("Promotion expiry reminder email sent successfully to userId: " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + e.getMessage());
        }
    }

    
    
  
}
