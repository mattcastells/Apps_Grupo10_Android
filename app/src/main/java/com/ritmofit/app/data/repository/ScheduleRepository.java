package com.ritmofit.app.data.repository;

import androidx.annotation.NonNull;

import com.ritmofit.app.data.api.ScheduleService;
import com.ritmofit.app.data.api.model.ScheduledClassDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleRepository {

    private ScheduleService scheduleService;

    public ScheduleRepository(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    public void getWeeklySchedule(final RepositoryCallback<List<ScheduledClassDto>> callback) {
        scheduleService.getWeeklySchedule().enqueue(new Callback<List<ScheduledClassDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<ScheduledClassDto>> call, @NonNull Response<List<ScheduledClassDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error al obtener horarios. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ScheduledClassDto>> call, @NonNull Throwable t) {
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }

    public void getClassDetail(String classId, final RepositoryCallback<ScheduledClassDto> callback) {
        scheduleService.getClassDetail(classId).enqueue(new Callback<ScheduledClassDto>() {
            @Override
            public void onResponse(@NonNull Call<ScheduledClassDto> call, @NonNull Response<ScheduledClassDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error al obtener detalle de clase. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ScheduledClassDto> call, @NonNull Throwable t) {
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }
}
