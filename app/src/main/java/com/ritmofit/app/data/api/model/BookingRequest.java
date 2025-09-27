package com.ritmofit.app.data.api.model;

import com.google.gson.annotations.SerializedName;

public class BookingRequest {
    @SerializedName("scheduledClassId")
    private String scheduledClassId;

    public BookingRequest() {}

    public BookingRequest(String scheduledClassId) {
        this.scheduledClassId = scheduledClassId;
    }

    public String getScheduledClassId() {
        return scheduledClassId;
    }

    public void setScheduledClassId(String scheduledClassId) {
        this.scheduledClassId = scheduledClassId;
    }
}