package com.annular.filmhook.controller;

import java.util.List;
import java.util.Map;

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
	public ResponseEntity<Response> paymentSuccess(@RequestParam Map<String, String> params) {

		params.forEach((k,v) -> System.out.println(k + " = " + v));

		String txnid = params.get("txnid");

		String amountStr = params.get("amount");
		Double amount = amountStr != null ? Double.parseDouble(amountStr) : 0.0;

		String promoteId = params.get("udf1");

		if (txnid == null || promoteId == null) {
			return ResponseEntity.badRequest()
					.body(new Response(-1, "Missing required parameters", null));
		}

		return ResponseEntity.ok(
				promoteAdService.updatePaymentSuccess(txnid, promoteId, amount)
				);
	}

	@PostMapping("/payment/failure")
	public ResponseEntity<Response> paymentFailed(@RequestParam Map<String, String> params) {
		String txnid = params.get("txnid");        // Merchant Ref ID
		String status = params.get("status");
		String amountStr = params.get("amount");
		Double amount = amountStr != null ? Double.parseDouble(amountStr) : 0.0;
		String promoteId = params.get("udf1");
		String mobile = params.get("udf2");
		String address = params.get("udf3");

		return ResponseEntity.ok(promoteAdService.updatePaymentFailed(txnid, promoteId, amount));
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
