package com.ritmofit.app.data.api.model;

import com.google.gson.annotations.SerializedName;

public class UserBookingDto {
    @SerializedName("bookingId")
    private String bookingId;
    @SerializedName("className")
    private String className;
    @SerializedName("classDateTime")
    private String classDateTime; // Usamos String para simplificar el parsing inicial
    @SerializedName("professor")
    private String professor;
    @SerializedName("status")
    private String status;

    // Constructor vacío
    public UserBookingDto() {}

    // Constructor completo
    public UserBookingDto(String bookingId, String className, String classDateTime, String professor, String status) {
        this.bookingId = bookingId;
        this.className = className;
        this.classDateTime = classDateTime;
        this.professor = professor;
        this.status = status;
    }

    // Getters y Setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getClassDateTime() { return classDateTime; }
    public void setClassDateTime(String classDateTime) { this.classDateTime = classDateTime; }

    public String getProfessor() { return professor; }
    public void setProfessor(String professor) { this.professor = professor; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Métodos de utilidad para formatear fecha y hora
    public String getFormattedDate() {
        if (classDateTime != null && classDateTime.length() >= 10) {
            return classDateTime.substring(0, 10); // YYYY-MM-DD
        }
        return "Fecha no disponible";
    }

    public String getFormattedTime() {
        if (classDateTime != null && classDateTime.length() >= 16) {
            return classDateTime.substring(11, 16); // HH:MM
        }
        return "Hora no disponible";
    }
}
