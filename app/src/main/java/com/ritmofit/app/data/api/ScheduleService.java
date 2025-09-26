package com.ritmofit.app.data.api;

import com.ritmofit.app.data.api.model.ScheduledClassDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ScheduleService {

    @GET("schedule/weekly")
    Call<List<ScheduledClassDto>> getWeeklySchedule();

    @GET("schedule/{classId}")
    Call<ScheduledClassDto> getClassDetails(@Path("classId") String classId);
}
