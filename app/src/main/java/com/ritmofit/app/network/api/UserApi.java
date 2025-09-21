package com.ritmofit.app.network.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import com.ritmofit.app.network.request.StudentRequest;
import com.ritmofit.app.network.request.UserRequest;

public interface UserApi {
    @POST("users/create")
    Call<Object> createUser(@Body UserRequest student);

    @PUT("users/{id}")
    Call<Object> updateUser(@Path("id") String id, @Body UserRequest user);

//    @POST("users/professor")
//    Call<Object> createProfessor(@Body ProfessorRequest professor);
}