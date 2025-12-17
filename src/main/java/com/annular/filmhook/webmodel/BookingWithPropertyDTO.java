package com.annular.filmhook.webmodel;

import lombok.*;

@Getter
@Setter
@Builder
public class BookingWithPropertyDTO {
	private ShootingLocationBookingDTO booking;
	private ShootingLocationPropertyDetailsDTO property;
}
