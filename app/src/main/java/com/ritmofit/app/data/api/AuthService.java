package com.ritmofit.app.data.api;

import com.ritmofit.app.data.api.model.AuthResponse;
import com.ritmofit.app.data.api.model.OtpRequest;
import com.ritmofit.app.data.api.model.OtpVerifyRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    @POST("auth/login")
    Call<Void> requestOtp(@Body OtpRequest otpRequest);

    @POST("auth/verify")
    Call<AuthResponse> verifyOtp(@Body OtpVerifyRequest otpVerifyRequest);

}