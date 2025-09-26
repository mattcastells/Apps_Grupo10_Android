package com.ritmofit.app.data.api;

import com.ritmofit.app.data.api.model.AuthResponse;
import com.ritmofit.app.data.api.model.LoginRequest;
import com.ritmofit.app.data.api.model.OtpRequest;
import com.ritmofit.app.data.api.model.OtpVerifyRequest;
import com.ritmofit.app.data.api.model.UserRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<Map<String, String>> register(@Body UserRequest userRequest);

    @POST("auth/verify-email")
    Call<Map<String, String>> verifyEmail(@Body OtpVerifyRequest otpVerifyRequest);
}