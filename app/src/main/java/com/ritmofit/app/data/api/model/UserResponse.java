package com.ritmofit.app.data.api.model;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
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

}
