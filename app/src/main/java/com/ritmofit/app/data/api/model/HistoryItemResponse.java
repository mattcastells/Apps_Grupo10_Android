package com.ritmofit.app.data.api.model;

public class HistoryItemResponse {
    public String id;
    public String discipline;     // "Funcional HIIT"
    public String teacher;        // "Laura Pérez"
    public String site;           // "Sede Centro"
    public String location;       // "Av. ... 123" (opcional)
    public String startDateTime;  // ISO-8601: "2025-09-15T18:00:00"
    public int durationMinutes;   // 60
}