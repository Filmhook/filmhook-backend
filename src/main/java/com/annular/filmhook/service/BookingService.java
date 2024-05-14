package com.annular.filmhook.service;

import com.annular.filmhook.webmodel.BookingWebModel;

import java.util.List;

public interface BookingService {

    BookingWebModel saveOrUpdateBookingRequest(BookingWebModel scheduleWebModel, boolean isUpdate);

    List<BookingWebModel> getBookingsByUserId(Integer userId);

}
