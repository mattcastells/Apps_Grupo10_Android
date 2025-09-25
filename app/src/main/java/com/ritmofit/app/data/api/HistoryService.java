package com.ritmofit.app.data.api;

import com.ritmofit.app.data.api.model.HistoryItemResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

// GET /history/users/{userId}?from=YYYY-MM-DD&to=YYYY-MM-DD
public interface HistoryService {
    @GET("history/users/{userId}")
    Call<List<HistoryItemResponse>> getMyHistory(
            @Path("userId") String userId,
            @Query("from") String fromDate,
            @Query("to")   String toDate
    );
}


// ATENCIÓN ¡¡¡!!! Para el backend: GET /api/v1/history/users/{userId}?from=YYYY-MM-DD&to=YYYY-MM-DD devuelve List<HistoryItemResponse>