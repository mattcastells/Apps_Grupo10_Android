package com.ritmofit.app.data.repository;

import androidx.annotation.NonNull;
import com.ritmofit.app.data.api.BookingService;
import com.ritmofit.app.data.api.model.BookingRequest;
import com.ritmofit.app.data.api.model.BookingResponse;
import com.ritmofit.app.data.api.model.UserBookingDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingRepository {

    private final BookingService bookingService;

    public BookingRepository(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public void getMyBookings(final RepositoryCallback<List<UserBookingDto>> callback) {
        bookingService.getMyBookings().enqueue(new Callback<List<UserBookingDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserBookingDto>> call, @NonNull Response<List<UserBookingDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error al obtener las reservas: Código " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserBookingDto>> call, @NonNull Throwable t) {
                callback.onError("Error de red al obtener las reservas: " + t.getMessage());
            }
        });
    }

    public void createBooking(String scheduledClassId, final RepositoryCallback<BookingResponse> callback) {
        BookingRequest request = new BookingRequest(scheduledClassId);
        
        bookingService.createBooking(request).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(@NonNull Call<BookingResponse> call, @NonNull Response<BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error al crear la reserva: Código " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookingResponse> call, @NonNull Throwable t) {
                callback.onError("Error de red al crear la reserva: " + t.getMessage());
            }
        });
    }

    public void cancelBooking(String bookingId, final RepositoryCallback<Void> callback) {
        bookingService.cancelBooking(bookingId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Error al cancelar la reserva: Código " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onError("Error de red al cancelar la reserva: " + t.getMessage());
            }
        });
    }
}
