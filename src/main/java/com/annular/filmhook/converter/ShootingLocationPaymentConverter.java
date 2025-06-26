package com.annular.filmhook.converter;

import org.springframework.stereotype.Component;

import com.annular.filmhook.model.ShootingLocationBooking;
import com.annular.filmhook.model.ShootingLocationPayment;
import com.annular.filmhook.util.HashGenerator;
import com.annular.filmhook.webmodel.ShootingLocationPayURequest;

@Component
public class ShootingLocationPaymentConverter {



	public ShootingLocationPayURequest toDto(ShootingLocationPayment payment) {
		ShootingLocationPayURequest dto = new ShootingLocationPayURequest();
		dto.setPaymentId(payment.getPaymentId());
		dto.setTxnid(payment.getTxnid());
		dto.setAmount(payment.getAmount());
		dto.setProductinfo(payment.getProductinfo());
		dto.setFirstname(payment.getFirstname());
		dto.setEmail(payment.getEmail());
		dto.setPhone(payment.getPhone());
		dto.setBookingId(payment.getBooking().getId());
		dto.setCreatedBy(payment.getBooking().getClient().getUserId());
		dto.setCreatedOn(payment.getCreatedOn());
		dto.setUpdatedOn(payment.getUpdatedOn());
		dto.setStatus(payment.getStatus());
		dto.setReason(payment.getReason());
		return dto;

	}}