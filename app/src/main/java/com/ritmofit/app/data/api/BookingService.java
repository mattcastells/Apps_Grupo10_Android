package com.ritmofit.app.data.api;

import com.ritmofit.app.data.api.model.BookingRequest;
import com.ritmofit.app.data.api.model.BookingResponse;
import com.ritmofit.app.data.api.model.UserBookingDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BookingService {

    @GET("booking/my-bookings")
    Call<List<UserBookingDto>> getMyBookings();

    @POST("booking")
    Call<BookingResponse> createBooking(@Body BookingRequest bookingRequest);

    @DELETE("booking/{bookingId}")
    Call<Void> cancelBooking(@Path("bookingId") String bookingId);
}
