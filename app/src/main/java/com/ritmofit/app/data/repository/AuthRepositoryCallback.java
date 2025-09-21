package com.ritmofit.app.data.repository;


public interface AuthRepositoryCallback<T> {
    void onSuccess(T response);
    void onError(String message);
}
