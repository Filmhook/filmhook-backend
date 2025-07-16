package com.annular.filmhook.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.Response;
import com.annular.filmhook.model.ShootingLocationBooking;
import com.annular.filmhook.model.ShootingLocationPayment;
import com.annular.filmhook.webmodel.ShootingLocationBookingDTO;
import com.annular.filmhook.webmodel.ShootingLocationChatDTO;
import com.annular.filmhook.webmodel.ShootingLocationPayURequest;


public interface ShootingLocationBookingService {
	
	
	ShootingLocationBookingDTO createBooking(ShootingLocationBookingDTO dto);

    List<ShootingLocationBookingDTO> getBookingsByClient(Integer clientId);

    List<ShootingLocationBookingDTO> getBookingsByProperty(Integer propertyId);
    

    ResponseEntity<?> saveShootingPayment(ShootingLocationPayURequest req);
    
//    public ResponseEntity<?> sendShootingLocationBookingMail(ShootingLocationPayURequest request);
    ResponseEntity<?> sendShootingLocationBookingEmail(ShootingLocationBooking booking, ShootingLocationPayment payment, boolean isSuccess);
    
    void sendBookingExpiryReminders();
    void markBookingsAsCompleted();
    
    List<LocalDate> getAvailableDatesForProperty(Integer propertyId);
    
    boolean canChatByProperty(Integer senderId, Integer receiverId, Integer propertyId);
    String sendMessage(ShootingLocationChatDTO dto, Integer propertyId);
    List<ShootingLocationChatDTO> getChatHistory(Integer senderId, Integer receiverId);
    
 


}
