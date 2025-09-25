package com.ritmofit.app.data.api.model;

public class HistoryDetailResponse {
    public String id;
    public String discipline;
    public String teacher;
    public String site;
    public String location;
    public String startDateTime;    // ISO-8601
    public int    durationMinutes;
    public String attendanceStatus; // "CONFIRMADA", "CANCELADA", etc.
    public Review userReview;       // null si no hay

    public static class Review {
        public Integer rating;      // 1..5
        public String comment;      // null si solo rating
        public String createdAt;    // opcional
    }
}