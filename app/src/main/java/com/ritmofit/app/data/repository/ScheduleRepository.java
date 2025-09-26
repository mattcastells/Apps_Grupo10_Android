package com.ritmofit.app.data.repository;

import com.ritmofit.app.data.RitmoFitApiService;
import com.ritmofit.app.data.api.ScheduleService;
import com.ritmofit.app.data.api.model.ScheduledClassDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleRepository {
    
    private ScheduleService scheduleService;
    
    public ScheduleRepository() {
        this.scheduleService = RitmoFitApiService.getClient().create(ScheduleService.class);
    }
    
    public void getWeeklySchedule(ScheduleCallback<List<ScheduledClassDto>> callback) {
        Call<List<ScheduledClassDto>> call = scheduleService.getWeeklySchedule();
        call.enqueue(new Callback<List<ScheduledClassDto>>() {
            @Override
            public void onResponse(Call<List<ScheduledClassDto>> call, Response<List<ScheduledClassDto>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code() + " - " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<List<ScheduledClassDto>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    public void getClassDetails(String classId, ScheduleCallback<ScheduledClassDto> callback) {
        Call<ScheduledClassDto> call = scheduleService.getClassDetails(classId);
        call.enqueue(new Callback<ScheduledClassDto>() {
            @Override
            public void onResponse(Call<ScheduledClassDto> call, Response<ScheduledClassDto> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code() + " - " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<ScheduledClassDto> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    public interface ScheduleCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }
}
