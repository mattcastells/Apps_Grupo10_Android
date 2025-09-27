package com.ritmofit.app.data.api.model;

import com.google.gson.annotations.SerializedName;

public class BookingResponse {
    @SerializedName("id")
    private String id;
    @SerializedName("className")
    private String className;
    @SerializedName("locationName")
    private String locationName;
    @SerializedName("classDateTime")
    private String classDateTime;
    @SerializedName("status")
    private String status;

    public BookingResponse() {}

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public String getClassDateTime() { return classDateTime; }
    public void setClassDateTime(String classDateTime) { this.classDateTime = classDateTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
