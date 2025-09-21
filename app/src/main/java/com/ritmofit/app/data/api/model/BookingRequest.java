package com.ritmofit.app.data.api.model;

import com.google.gson.annotations.SerializedName;

public class BookingRequest {
    @SerializedName("id_user")
    public String id_user;
    @SerializedName("id_class")
    public String id_class;
}