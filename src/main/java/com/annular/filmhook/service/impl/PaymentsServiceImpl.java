package com.annular.filmhook.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.annular.filmhook.model.Payments;
import com.annular.filmhook.repository.PaymentsRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.PaymentsService;
import com.annular.filmhook.util.HashGenerator;
import com.annular.filmhook.webmodel.PaymentsDTO;

@Service
public class PaymentsServiceImpl implements PaymentsService {
	@Autowired
	private PaymentsRepository paymentsRepository;

	@Autowired
	private UserRepository userRepository;

	@Value("${payu.key}")
	private String key;

	@Value("${payu.salt}")
	private String salt;

	public Payments createPayment(PaymentsDTO dto) {

		if (dto.getUserId() == null) {
			throw new RuntimeException("User id must not be null");
		}

		if (!userRepository.existsById(dto.getUserId())) {
			throw new RuntimeException("User not found");
		}

		// A: Generate or validate txnid
		if (dto.getTxnid() == null || dto.getTxnid().trim().isEmpty()) {

			String txnid;
			do {
				txnid = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
			} while (paymentsRepository.existsByTxnid(txnid));

			dto.setTxnid(txnid);

		} else {
			if (paymentsRepository.existsByTxnid(dto.getTxnid())) {
				throw new IllegalArgumentException("Duplicate transaction ID: " + dto.getTxnid());
			}
		}

		// B: Generate PayU hash
		String productInfo = dto.getProductInfo() != null ? dto.getProductInfo() : dto.getModuleType().name();
		String hash = HashGenerator.generateHash(
				key,
				dto.getTxnid(),
				String.format("%.2f", dto.getAmount()),
				productInfo,
				dto.getFullName(),
				dto.getEmail(),
				salt
				);

		// C: Create payment object
		Payments payment = Payments.builder()
				.referenceId(dto.getReferenceId())
				.moduleType(dto.getModuleType())
				.userId(dto.getUserId())
				.fullName(dto.getFullName())
				.email(dto.getEmail())
				.amount(dto.getAmount())
				.paymentStatus("PENDING")
				.paymentHash(hash)
				.phoneNumber(dto.getPhoneNumber())
				.txnid(dto.getTxnid())
				.createdOn(LocalDateTime.now())
				.paymentGateway("PAYU")
				.createdBy(dto.getUserId())
				.build();

		return paymentsRepository.save(payment);
	}

	// 2️⃣ PAYMENT SUCCESS
	public Payments markPaymentSuccess(String txnid) {

		Payments payment = paymentsRepository.findByTxnid(txnid)
				.orElseThrow(() -> new RuntimeException("Payment not found"));

		payment.setPaymentStatus("SUCCESS");

		return paymentsRepository.save(payment);
	}

	public Payments markPaymentFailure(String txnid, String reason) {

	    Payments payment = paymentsRepository.findByTxnid(txnid)
	            .orElseThrow(() -> new RuntimeException("Payment not found"));

	    payment.setPaymentStatus("FAILED");
	    payment.setReason(reason);

	    return paymentsRepository.save(payment);
	}


	// 4️⃣ EXPIRY CHECK
	public List<Payments> getExpiredPayments() {
		return paymentsRepository.findByPaymentStatusAndExpiryDateBefore(
				"SUCCESS",
				LocalDateTime.now()
				);
	}
	public Payments markExpired(Payments payment, LocalDateTime expiryDateTime) {

	    payment.setExpiryDate(expiryDateTime);
	    payment.setPaymentStatus("EXPIRED");
	    payment.setUpdatedOn(LocalDateTime.now());
	    payment.setUpdatedBy(0); // system

	    return paymentsRepository.save(payment);
	}

}

