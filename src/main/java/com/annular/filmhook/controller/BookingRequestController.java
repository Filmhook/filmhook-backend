package com.annular.filmhook.controller;

import com.annular.filmhook.Response;
import com.annular.filmhook.service.BookingService;
import com.annular.filmhook.util.Utility;
import com.annular.filmhook.webmodel.BookingWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/booking")
public class BookingRequestController {

    public static final Logger logger = LoggerFactory.getLogger(BookingRequestController.class);

    @Autowired
    BookingService bookingService;

    @PostMapping("/saveBookingRequest")
    public Response saveBookingRequest(@RequestBody BookingWebModel bookingWebModel) {
        try {
            BookingWebModel savedBookingRequest = bookingService.saveOrUpdateBookingRequest(bookingWebModel, false);
            if (savedBookingRequest != null) {
                if (Utility.isNullOrBlankWithTrim(savedBookingRequest.getErrorMsg())) {
                    return new Response(1, "Booking request saved successfully...", savedBookingRequest);
                } else {
                    return new Response(-1, "Error at booking request save...", savedBookingRequest.getErrorMsg());
                }
            }
        } catch (Exception e) {
            logger.error("Error at saveBookingRequest -> {}", e.getMessage());
            e.printStackTrace();
            return new Response(-1, "Error at saving the booking request...", null);
        }
        return new Response(-1, "Error at saving the booking request...", null);
    }

    @GetMapping("/getBookingsByUserId")
    public Response getBookingsByUserId(@RequestParam("userId") Integer userId) {
        try {
            List<BookingWebModel> userSchedules = bookingService.getBookingsByUserId(userId);
            if (!Utility.isNullOrEmptyList(userSchedules))
                return new Response(1, "User bookings retrieved successfully...", userSchedules);
        } catch (Exception e) {
            logger.error("Error at getBookingsByUserId -> {}", e.getMessage());
            e.printStackTrace();
            return new Response(-1, "Error at getting user bookings...", null);
        }
        return new Response(-1, "Error at getting user bookings...", null);
    }

    @PostMapping("/acceptBookingRequest")
    public Response acceptBookingRequest(@RequestBody BookingWebModel bookingWebModel) {
        try {
            BookingWebModel updatedBookingRequest = bookingService.saveOrUpdateBookingRequest(bookingWebModel, true);
            if (updatedBookingRequest != null)
                return new Response(1, "Booking request accepted successfully...", updatedBookingRequest);
        } catch (Exception e) {
            logger.error("Error at acceptBookingRequest -> {}", e.getMessage());
            e.printStackTrace();
            return new Response(-1, "Error at updating the booking request...", null);
        }
        return new Response(-1, "Error at updating the booking request...", null);
    }

}
