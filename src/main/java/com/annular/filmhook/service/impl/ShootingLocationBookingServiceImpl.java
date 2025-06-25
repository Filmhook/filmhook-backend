package com.annular.filmhook.service.impl;

import com.annular.filmhook.converter.ShootingLocationBookingConverter;
import com.annular.filmhook.model.BookingStatus;
import com.annular.filmhook.model.ShootingLocationBooking;
import com.annular.filmhook.model.ShootingLocationPropertyDetails;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.ShootingLocationBookingRepository;
import com.annular.filmhook.repository.ShootingLocationPropertyDetailsRepository;
import com.annular.filmhook.repository.UserRepository;
import com.annular.filmhook.service.ShootingLocationBookingService;
import com.annular.filmhook.webmodel.ShootingLocationBookingDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShootingLocationBookingServiceImpl implements ShootingLocationBookingService {

    @Autowired
    private ShootingLocationBookingRepository bookingRepository;

    @Autowired
    private ShootingLocationPropertyDetailsRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ShootingLocationBookingDTO createBooking(ShootingLocationBookingDTO dto) {
        // Check if property exists
        ShootingLocationPropertyDetails property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));

        // Check if client exists
        User client = userRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

        // Check if already confirmed booking exists for this property
        bookingRepository.findByProperty_IdAndStatus(dto.getPropertyId(), BookingStatus.CONFIRMED)
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "This property has already been confirmed as booked.");
                });

        // Check if same client already booked (any status)
        List<ShootingLocationBooking> bookings = bookingRepository.findByProperty_IdAndClient_UserId(dto.getPropertyId(), dto.getClientId());
        if (!bookings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already requested booking for this property.");
        }

        // Create and save booking
        ShootingLocationBooking entity = ShootingLocationBookingConverter.toEntity(dto, client, property);
        ShootingLocationBooking saved = bookingRepository.save(entity);

        return ShootingLocationBookingConverter.toDTO(saved);
    }

    @Override
    public List<ShootingLocationBookingDTO> getBookingsByClient(Integer clientId) {
        return bookingRepository.findByClient_UserId(clientId)
                .stream()
                .map(ShootingLocationBookingConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShootingLocationBookingDTO> getBookingsByProperty(Integer propertyId) {
        return bookingRepository.findByProperty_Id(propertyId)
                .stream()
                .map(ShootingLocationBookingConverter::toDTO)
                .collect(Collectors.toList());
    }
}
