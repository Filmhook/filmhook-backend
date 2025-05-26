package com.annular.filmhook.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.annular.filmhook.configuration.PaymentConfig;
import com.annular.filmhook.model.PaymentWebModel;
import com.annular.filmhook.service.PaymentService;

import java.util.HashMap;
import java.util.Map;

import com.cashfree.Cashfree;
import com.cashfree.ApiResponse;
import com.cashfree.ApiException;
import com.cashfree.model.CreateOrderRequest;
import com.cashfree.model.CustomerDetails;
import com.cashfree.model.OrderEntity;

@Service
public class PaymentServiceImpl implements PaymentService {

    public static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    private PaymentConfig paymentConfig;

    @Override
    public PaymentWebModel generatePaymentToken(PaymentWebModel paymentWebModel) throws Exception {
        try {
            Cashfree.XClientId = paymentConfig.getClientId();
            Cashfree.XClientSecret = paymentConfig.getClientSecret();
            Cashfree.XEnvironment = Cashfree.SANDBOX; // Cashfree.PRODUCTION :: For Live

            CustomerDetails customerDetails = new CustomerDetails();
            customerDetails.setCustomerId(paymentConfig.getCustomerId());
            customerDetails.setCustomerPhone(paymentConfig.getCustomerPhone());

            CreateOrderRequest request = new CreateOrderRequest();
            request.setOrderAmount(paymentWebModel.getOrderAmount());
            request.setOrderCurrency(paymentWebModel.getOrderCurrency());
            request.setCustomerDetails(customerDetails);

            Cashfree cashfree = new Cashfree();
            ApiResponse<OrderEntity> response = cashfree.PGCreateOrder("2023-08-01", request, null, null, null);
            // Sample Response from cashfree integration docs
            /*{
                "cf_order_id": "1539553",
                "created_at": "2021-07-19T16:13:35+05:30",
                "customer_details": {
                    "customer_id": "7112AAA812234",
                    "customer_name": null,
                    "customer_email": "john@cashfree.com",
                    "customer_phone": "9908734801",
                    "customer_uid": null
                },
                "entity": "order",
                "order_amount": 5.01,
                "order_currency": "INR",
                "order_expiry_time": "2021-08-18T16:13:34+05:30",
                "order_id": "order_271vWwzSQOHe01ZVXpEcguVxQSRqr",
                "order_meta": {
                    "return_url": "https://b8af79f41056.eu.ngrok.io?order_id=order_123",
                    "payment_methods": null
                },
                "order_note": null,
                "order_status": "PAID",
                "payment_session_id": "session_7NvteR73Fh11P3f3bNdcubIAJgBJJgGK9diC6U5jvr_jfWBS8o-Z2iPf20diqBMVfWDwvARGrISZRCPoDSWjw4Eb1GrKtoZZQT_BWyXW25fD"
            }*/
            logger.info(response.getData().toJson()); // Actual Response from cashfree
            OrderEntity orderResponse = response.getData();
            return this.transformOrderEntityResponseToPaymentWebModel(orderResponse);
        } catch (ApiException e) {
            throw new Exception("Error at generatePaymentToken()...", e);
        }
    }

    private PaymentWebModel transformOrderEntityResponseToPaymentWebModel(OrderEntity orderResponse) {
        Map<String, Object> customerDetails = new HashMap<>();
        if (orderResponse.getCustomerDetails() != null) {
            customerDetails.put("customerId", orderResponse.getCustomerDetails().getCustomerId());
            customerDetails.put("customerName", orderResponse.getCustomerDetails().getCustomerName());
            customerDetails.put("customerEmail", orderResponse.getCustomerDetails().getCustomerEmail());
            customerDetails.put("customerPhone", orderResponse.getCustomerDetails().getCustomerPhone());
            customerDetails.put("customerUid", orderResponse.getCustomerDetails().getCustomerUid());
        }

        Map<String, Object> orderMetaDetails = new HashMap<>();
        if (orderResponse.getOrderMeta() != null) {
            customerDetails.put("returnUrl", orderResponse.getOrderMeta().getReturnUrl());
            customerDetails.put("paymentMethods", orderResponse.getOrderMeta().getPaymentMethods());
        }

        return PaymentWebModel.builder()
                .cfOrderId(orderResponse.getCfOrderId())
                .createdAt(orderResponse.getCreatedAt() != null ? orderResponse.getCreatedAt().toString() : "")
                .customerDetails(customerDetails)
                .entity(orderResponse.getEntity())
                .orderAmount(Double.parseDouble(String.valueOf(orderResponse.getOrderAmount())))
                .orderCurrency(orderResponse.getOrderCurrency())
                .orderExpiryTime(orderResponse.getOrderExpiryTime() != null ? orderResponse.getOrderExpiryTime().toString() : "")
                .orderId(orderResponse.getOrderId())
                .orderMeta(orderMetaDetails)
                .orderNote(orderResponse.getOrderNote())
                .orderStatus(orderResponse.getOrderStatus())
                .paymentSessionId(orderResponse.getPaymentSessionId())
                .build();
    }

}
