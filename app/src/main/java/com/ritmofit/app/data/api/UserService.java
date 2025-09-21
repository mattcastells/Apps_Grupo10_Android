package com.ritmofit.app.data.api;

import com.ritmofit.app.data.api.model.UserRequest;
import com.ritmofit.app.data.api.model.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {

    @GET("users/{id}")
    Call<UserResponse> getUser(@Body UserRequest user);

    @POST("users")
    Call<Void> createUser(@Body UserRequest user);

    @PUT("users/{id}")
    Call<Void> updateUser(@Path("id") String id, @Body UserRequest user);
}
