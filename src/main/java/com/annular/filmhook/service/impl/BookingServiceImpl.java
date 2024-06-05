package com.annular.filmhook.service.impl;

import com.annular.filmhook.UserDetails;
import com.annular.filmhook.model.Bookings;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.BookingsRepository;
import com.annular.filmhook.service.BookingService;
import com.annular.filmhook.service.NotificationService;
import com.annular.filmhook.util.Utility;
import com.annular.filmhook.webmodel.BookingWebModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BookingServiceImpl implements BookingService {

    public static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    @Autowired
    UserDetails loggedInUser;

    @Autowired
    BookingsRepository bookingsRepository;

    @Autowired
    NotificationService notificationService;

    @Override
    public BookingWebModel saveOrUpdateBookingRequest(BookingWebModel bookingWebModel, boolean isUpdate) {
        try {
            Bookings bookings = null;
            User currentUser = User.builder().userId(bookingWebModel.getCurrentUserId()).build();
            User bookingUser = User.builder().userId(bookingWebModel.getBookingUserId()).build();

            if (currentUser.getUserId().equals(bookingUser.getUserId()))
                return BookingWebModel.builder().errorMsg("Current UserId and Booking UserId cannot be the same...").build();

            if (isUpdate && bookingWebModel.getBookingId() != null) {
                Bookings existingBooking = bookingsRepository.findById(bookingWebModel.getBookingId()).orElse(null);
                if (existingBooking != null) {
                    existingBooking.setBookingStatus(bookingWebModel.getBookingStatus()); // Updating the existing row with new status
                    bookings = existingBooking;
                    logger.info("Existing bookings object updated for bookings...");
                }
            } else {
                // Checking User dates to prevent date clash
                List<Bookings> userBookings = bookingsRepository.getPendingBookingsByUserFromAndToDates(bookingUser, bookingWebModel.getFromDate(), bookingWebModel.getToDate());
                if(!Utility.isNullOrEmptyList(userBookings)) return BookingWebModel.builder().errorMsg("From and to dates are not available for this user...").build();

                bookings = Bookings.builder()
                        .project(bookingWebModel.getProjectName())
                        .bookedBy(currentUser)
                        .bookedUser(bookingUser)
                        .fromDate(bookingWebModel.getFromDate())
                        .toDate(bookingWebModel.getToDate())
                        .bookingStatus(bookingWebModel.getBookingStatus())
                        .status(true)
                        .createdBy((loggedInUser != null && loggedInUser.userInfo() != null) ? loggedInUser.userInfo().getId() : null)
                        .createdOn(new Date())
                        .updatedBy((loggedInUser != null && loggedInUser.userInfo() != null) ? loggedInUser.userInfo().getId() : null)
                        .updatedOn(new Date())
                        .build();
                logger.info("New object created for bookings...");
            }

            if (bookings != null) {
                Bookings savedBookingRequest = bookingsRepository.saveAndFlush(bookings); // Save or Update
                if (!isUpdate) notificationService.sendBookingRequestNotifications(savedBookingRequest); // Sending Notifications
                return this.transformBookingData(List.of(savedBookingRequest)).get(0);
            }
        } catch (Exception e) {
            logger.error("Error at saveOrUpdateBookingRequest() -> {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public List<BookingWebModel> getBookingsByUserId(Integer userId) {
        try {
            User userToSearch = User.builder().userId(userId).build();
            return this.transformBookingData(bookingsRepository.findByBookedUser(userToSearch));
        } catch (Exception e) {
            logger.error("Error at getBookingsByUserId() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private List<BookingWebModel> transformBookingData(List<Bookings> userBookings) {
        List<BookingWebModel> bookingWebModels = new ArrayList<>();
        try {
            if (!Utility.isNullOrEmptyList(userBookings)) {
                userBookings.stream()
                        .filter(Objects::nonNull)
                        .forEach(booking -> {
                            BookingWebModel bookingWebModel = BookingWebModel.builder()
                                    .bookingId(booking.getId())
                                    .projectName(booking.getProject())
                                    .currentUserId(booking.getBookedBy().getUserId())
                                    .bookingUserId(booking.getBookedUser().getUserId())
                                    .fromDate(booking.getFromDate())
                                    .toDate(booking.getToDate())
                                    .bookingStatus(booking.getBookingStatus())
                                    .active(booking.getStatus())
                                    .createdBy(booking.getCreatedBy())
                                    .createdOn(booking.getCreatedOn())
                                    .build();
                            bookingWebModels.add(bookingWebModel);
                        });
            }
        } catch (Exception e) {
            logger.error("Error at transformBookingData() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return bookingWebModels;
    }

    @Override
    public List<BookingWebModel> getConfirmedBookingsByUserId(Integer userId) {
        try {
            User userToSearch = User.builder().userId(userId).build();
            return this.transformBookingData(bookingsRepository.getConfirmedBookingsByBookedUser(userToSearch));
        } catch (Exception e) {
            logger.error("Error at getAcceptedBookingsByUserId() -> {}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
