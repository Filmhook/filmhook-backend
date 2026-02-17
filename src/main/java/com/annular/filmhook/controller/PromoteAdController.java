package com.annular.filmhook.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.annular.filmhook.Response;
import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.PromoteAd;
import com.annular.filmhook.service.PromoteAdService;
import com.annular.filmhook.util.HashGenerator;
import com.annular.filmhook.webmodel.PromoteWebModel;
import com.annular.filmhook.webmodel.VisitPageWebModel;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/promote")
@RequiredArgsConstructor
public class PromoteAdController {

	private final PromoteAdService promoteAdService;
	@Autowired
	private  UserDetails userDetails;

	@Autowired
	private HashGenerator hashGenerator;


	@PostMapping("/save")
	public ResponseEntity<Response> savePromote(
			@ModelAttribute PromoteWebModel model) {

		return ResponseEntity.ok(promoteAdService.savePromote(model));

	}

	@GetMapping("/post/{postId}")
	public ResponseEntity<?> getPromoteData(@PathVariable Integer postId) { 

		PromoteAd result = promoteAdService.getPromoteByPostId(postId);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/payment/success")
	public void paymentSuccess(@RequestParam Map<String, String> params,
	                           HttpServletResponse response) throws IOException {

	    String txnid = params.get("txnid");
	    String status = params.get("status");
	    String amountStr = params.get("amount");
	    String receivedHash = params.get("hash");
	    String promoteId = params.get("udf1");
	    String realStatus = params.get("field9");

	    if (txnid == null || promoteId == null || status == null) {
	        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	        return;
	    }

	    // 🔐 Verify hash
	    String calculatedHash = hashGenerator.generateResponseHash(params);

	    if (!calculatedHash.equals(receivedHash)) {
	        response.sendRedirect("filmhook://promote-payment-failure?txnid=" + txnid);
	        return;
	    }

	    if (!"success".equalsIgnoreCase(status)) {
	        response.sendRedirect("filmhook://promote-payment-failure?txnid=" + txnid);
	        return;
	    }

	    BigDecimal amount = new BigDecimal(amountStr);

	    promoteAdService.updatePaymentSuccess(txnid, promoteId, amount);

	    // ✅ Success time (epoch millis)
	    long successTime = System.currentTimeMillis();

	    // 🔥 Send all values to frontend
	    String redirectUrl = String.format(
	            "filmhook://promote-payment-success?txnid=%s&amount=%s&status=%s&realStatus=%s&time=%d",
	            txnid,
	            amountStr,
	            status,
	            realStatus,
	            successTime
	    );
	    System.out.println("Payment success redirect   "+ redirectUrl);

	    response.sendRedirect(redirectUrl);
	}



	@PostMapping("/payment/failure")
	public void paymentFailure(@RequestParam Map<String, String> params,
	                           HttpServletResponse response) throws IOException {

	    String txnid = params.get("txnid");
	    String status = params.get("status");
	    String amountStr = params.get("amount");
	    String receivedHash = params.get("hash");
	    String promoteId = params.get("udf1");
	    String realStatus = params.get("field9");

	    if (txnid == null || status == null) {
	        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	        return;
	    }

	    // 🔐 Verify Response Hash
	    String calculatedHash = hashGenerator.generateResponseHash(params);

	    if (!calculatedHash.equals(receivedHash)) {
	        response.sendRedirect("filmhook://promote-payment-failure?txnid=" + txnid);
	        return;
	    }

	    // ✅ Convert amount safely
	    BigDecimal amount = BigDecimal.ZERO;
	    if (amountStr != null) {
	        amount = new BigDecimal(amountStr);
	    }

	    // ✅ Update DB as FAILED
	    promoteAdService.updatePaymentFailed(txnid, promoteId, amount);

	    // ✅ Failure time
	    long failureTime = System.currentTimeMillis();

	    // 🔥 Send values to frontend
	    String redirectUrl = String.format(
	            "filmhook://promote-payment-failure?txnid=%s&amount=%s&status=%s&realStatus=%s&time=%d",
	            txnid,
	            amountStr,
	            status,
	            realStatus,
	            failureTime
	    );
	    
	    System.out.println("Payment failure redirect   "+ redirectUrl);

	    response.sendRedirect(redirectUrl);
	}



	// ⭐ GET RECENT PROMOTED (Running + Completed only)

	@GetMapping("/recent")
	public ResponseEntity<Response> getRecentPromoted() {

		Integer userId = userDetails.userInfo().getId();

		return ResponseEntity.ok(promoteAdService.getRecentPromotions(userId));
	}


	@GetMapping("/getObjectiveTypes")
	public ResponseEntity<List<VisitPageWebModel>> getAllCategories() {
		return ResponseEntity.ok(promoteAdService.getAllObjectives());
	}

	@GetMapping("/category/{categoryId}")
	public ResponseEntity<List<VisitPageWebModel>> getPagesByCategory(@PathVariable Integer categoryId) {
		return ResponseEntity.ok(promoteAdService.getPagesByCategoryId(categoryId));
	}

	@GetMapping("/page/{visitPageId}/details")
	public ResponseEntity<List<VisitPageWebModel>> getVisitPageDetails(
			@PathVariable Integer visitPageId) {
		return ResponseEntity.ok(promoteAdService.getDetailsByVisitPageId(visitPageId));
	}


}
