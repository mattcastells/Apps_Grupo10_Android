package com.ritmofit.app.network.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserRequest {
    @SerializedName("email")
    public String email;
    @SerializedName("name")
    public String name;
    @SerializedName("age")
    public Integer age;
    @SerializedName("gender")
    public String gender;
    @SerializedName("profilePicture")
    public String profilePicture;
    @SerializedName("password")
    public String password;

    @SerializedName("currentBookings")
    public List<Object> currentBookings = null;
    @SerializedName("classHistory")
    public List<Object> classHistory = null;
}
