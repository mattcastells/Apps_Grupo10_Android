package com.ritmofit.app.data.api.model;

import com.google.gson.annotations.SerializedName;

public class ScheduledClassDto {
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("professor")
    private String professor;
    
    @SerializedName("dateTime")
    private String dateTime;
    
    @SerializedName("durationMinutes")
    private Integer durationMinutes;
    
    @SerializedName("availableSlots")
    private Integer availableSlots;

    // Constructors
    public ScheduledClassDto() {}

    public ScheduledClassDto(String id, String name, String professor, String dateTime, Integer durationMinutes, Integer availableSlots) {
        this.id = id;
        this.name = name;
        this.professor = professor;
        this.dateTime = dateTime;
        this.durationMinutes = durationMinutes;
        this.availableSlots = availableSlots;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(Integer availableSlots) {
        this.availableSlots = availableSlots;
    }
}
