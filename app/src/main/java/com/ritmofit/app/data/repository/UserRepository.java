package com.ritmofit.app.data.repository;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ritmofit.app.data.api.UserService;
import com.ritmofit.app.data.api.model.OtpRequest;
import com.ritmofit.app.data.api.model.UpdatePhotoRequest;
import com.ritmofit.app.data.api.model.UserRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private UserService userService;

    public UserRepository(UserService userService) {
        this.userService = userService;
    }


    public void updateUser(String userId, String name, String email, String ageStr, String gender,
                           String profilePicture, String password, final RepositoryCallback<Void> callback) {
        UserRequest req = new UserRequest(
                name,
                email,
                Integer.valueOf(ageStr),
                gender,
                password
        );

        userService.updateUser(userId, req).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Error al guardar cambios: Código " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }

    public void updateUserPhoto(String id, String photoUrl, RepositoryCallback<Void> callback) {
        UserService api =
                com.ritmofit.app.data.RitmoFitApiService.getClient().create(UserService.class);
        UpdatePhotoRequest body =
                new UpdatePhotoRequest(photoUrl);

        api.updateUserPhoto(id, body).enqueue(new retrofit2.Callback<Void>() {
            @Override public void onResponse(@NonNull retrofit2.Call<Void> call, @NonNull retrofit2.Response<Void> response) {
                if (response.isSuccessful()) callback.onSuccess(null);
                else callback.onError("Error al actualizar foto: " + response.code());
            }
            @Override public void onFailure(@NonNull retrofit2.Call<Void> call, @NonNull Throwable t) {
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }
}