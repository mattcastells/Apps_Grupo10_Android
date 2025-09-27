package com.ritmofit.app.data.repository;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import androidx.annotation.NonNull;
import com.ritmofit.app.data.api.model.AuthResponse;
import com.ritmofit.app.data.api.model.LoginRequest;
import com.ritmofit.app.data.api.model.OtpRequest;
import com.ritmofit.app.data.api.AuthService;
import com.ritmofit.app.data.api.model.OtpVerifyRequest;
import com.ritmofit.app.data.api.model.UserRequest;

import java.util.Map;

public class AuthRepository {

    private AuthService authService;

    public AuthRepository(AuthService authService) {
        this.authService = authService;
    }


    // Agregamos el método de login con contraseña
    public void login(String email, String password, final RepositoryCallback<AuthResponse> callback) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        authService.login(loginRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Usuario o contraseña inválidos.");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void createUser(String email, String password, String name, String ageStr, String gender,
                           final RepositoryCallback<Void> callback) {

        UserRequest req = new UserRequest(
                name,
                email,
                Integer.valueOf(ageStr),
                gender,
                password
        );

        authService.register(req).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, String>> call, @NonNull Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Error al crear usuario: Código " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, String>> call, @NonNull Throwable t) {
                // Ocurrió un error de red (sin conexión, timeout, etc.)
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }

    public void verifyEmail(String email, String otp, final RepositoryCallback<Map<String, String>> callback) {
        OtpVerifyRequest request = new OtpVerifyRequest(email, otp);
        authService.verifyEmail(request).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    // Aquí podrías parsear un mensaje de error más específico del body si el backend lo envía
                    callback.onError("El código OTP es incorrecto o ha expirado.");
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                callback.onError("Error de red. Intenta nuevamente.");
            }
        });
    }

}