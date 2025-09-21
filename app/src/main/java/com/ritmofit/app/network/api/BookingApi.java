package com.ritmofit.app.network.api;

import com.ritmofit.app.network.request.BookingRequest;
import com.ritmofit.app.network.request.UserRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BookingApi {

    @POST("book/{id_user}/{id_class}")
    Call<Object> bookClass(@Path("id_user") String id_user, @Path("id_class") String id_class);

}