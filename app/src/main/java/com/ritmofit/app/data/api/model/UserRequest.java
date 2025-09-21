package com.ritmofit.app.data.api.model;

import com.google.gson.annotations.SerializedName;

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
    public String profilePicture = null;
    @SerializedName("password")
    public String password;

    public UserRequest(String name, String email, Integer age, String gender, String password) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.password = password;
    }

}
