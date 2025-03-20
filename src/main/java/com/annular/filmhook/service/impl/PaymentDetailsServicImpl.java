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
                .key("oXregF")
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
}
