package com.annular.filmhook.service;

import com.annular.filmhook.model.PaymentWebModel;

public interface PaymentService {

    PaymentWebModel generatePaymentToken(PaymentWebModel paymentWebModel) throws Exception;

    
    

}
