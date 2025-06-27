package com.annular.filmhook.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.Response;
import com.annular.filmhook.webmodel.ShootingLocationBookingDTO;
import com.annular.filmhook.webmodel.ShootingLocationPayURequest;


public interface ShootingLocationBookingService {
	
	
	ShootingLocationBookingDTO createBooking(ShootingLocationBookingDTO dto);

    List<ShootingLocationBookingDTO> getBookingsByClient(Integer clientId);

    List<ShootingLocationBookingDTO> getBookingsByProperty(Integer propertyId);
    

    ResponseEntity<?> saveShootingPayment(ShootingLocationPayURequest req);
    
    public ResponseEntity<?> sendShootingLocationBookingMail(ShootingLocationPayURequest request);
    
    void sendBookingExpiryReminders();
    void markBookingsAsCompleted();
    
    List<LocalDate> getAvailableDatesForProperty(Integer propertyId);

}
