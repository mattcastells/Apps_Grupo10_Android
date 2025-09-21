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

    private AuthService apiService;

    public AuthRepository(AuthService apiService) {
        this.apiService = apiService;
    }


    public void requestOtp(String email, final AuthRepositoryCallback<Void> callback) {
        OtpRequest requestBody = new OtpRequest(email);

        apiService.requestOtp(requestBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    // La llamada fue exitosa. Notificamos a través del callback.
                    callback.onSuccess(null); // Usamos null porque no hay datos que devolver
                } else {
                    // El servidor respondió con un error (email no encontrado, etc.)
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
    public void verifyOtp(String email, String otp, final AuthRepositoryCallback<AuthResponse> callback) {
        OtpVerifyRequest requestBody = new OtpVerifyRequest(email, otp);

        apiService.verifyOtp(requestBody).enqueue(new Callback<AuthResponse>() {
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