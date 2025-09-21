package com.ritmofit.app.data.repository;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import androidx.annotation.NonNull;
import com.ritmofit.app.data.api.model.AuthResponse;
import com.ritmofit.app.data.api.model.OtpRequest;
import com.ritmofit.app.data.api.AuthService;
import com.ritmofit.app.data.api.model.OtpVerifyRequest;

public class AuthRepository {

    private AuthService authService;

    public AuthRepository(AuthService authService) {
        this.authService = authService;
    }


    public void requestOtp(String email, final RepositoryCallback<Void> callback) {
        OtpRequest requestBody = new OtpRequest(email);

        authService.requestOtp(requestBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Error al solicitar el código. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // Ocurrió un error de red (sin conexión, timeout, etc.)
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }

    /**
     * Verifica el código OTP en el backend.
     * @param email El email del usuario.
     * @param otp El código OTP ingresado.
     * @param callback El callback para notificar el resultado.
     */
    public void verifyOtp(String email, String otp, final RepositoryCallback<AuthResponse> callback) {
        OtpVerifyRequest requestBody = new OtpVerifyRequest(email, otp);

        authService.verifyOtp(requestBody).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body()); // Devolvemos la respuesta (ej. token de sesión)
                } else {
                    callback.onError("OTP inválido o expirado. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }
}