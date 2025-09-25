package com.ritmofit.app.data.repository;

import android.content.Context;
import androidx.annotation.NonNull;
import com.ritmofit.app.data.RitmoFitApiService;
import com.ritmofit.app.data.api.HistoryService;
import com.ritmofit.app.data.api.model.HistoryItemResponse;
import com.ritmofit.app.data.session.SessionManager;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryRepository {
    private final HistoryService api;
    private final SessionManager sm;

    public HistoryRepository(Context ctx) {
        this.api = RitmoFitApiService.getClient(ctx).create(HistoryService.class);
        this.sm = new SessionManager(ctx.getApplicationContext());
    }

    public void getMyHistory(String from, String to,
                             RepositoryCallback<List<HistoryItemResponse>> cb) {
        String userId = sm.getUserId();
        if (userId == null || userId.isEmpty()) {
            cb.onError("No hay userId en sesión");
            return;
        }

        api.getMyHistory(userId, from, to).enqueue(new Callback<List<HistoryItemResponse>>() {
            @Override public void onResponse(@NonNull Call<List<HistoryItemResponse>> call,
                                             @NonNull Response<List<HistoryItemResponse>> resp) {
                if (resp.isSuccessful() && resp.body()!=null) cb.onSuccess(resp.body());
                else cb.onError("Error " + resp.code());
            }
            @Override public void onFailure(@NonNull Call<List<HistoryItemResponse>> call,
                                            @NonNull Throwable t) {
                cb.onError("Error de red: " + t.getMessage());
            }
        });
    }
}