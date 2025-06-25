package com.annular.filmhook.service;

import java.util.List;

import com.annular.filmhook.webmodel.ShootingLocationBookingDTO;


public interface ShootingLocationBookingService {
	
	
	ShootingLocationBookingDTO createBooking(ShootingLocationBookingDTO dto);

    List<ShootingLocationBookingDTO> getBookingsByClient(Integer clientId);

    List<ShootingLocationBookingDTO> getBookingsByProperty(Integer propertyId);

}
