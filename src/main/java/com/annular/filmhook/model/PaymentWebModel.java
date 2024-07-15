package com.annular.filmhook.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentWebModel {

    private String cfOrderId;
    private String createdAt;
    private Map<String, Object> customerDetails;

    private String entity;

    private Double orderAmount;
    private String orderCurrency;
    private String orderExpiryTime;
    private String orderId;
    private Map<String, Object> orderMeta;
    private String orderNote;
    private String orderStatus;

    private String paymentSessionId;

}
