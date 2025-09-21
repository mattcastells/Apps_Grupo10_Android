package com.ritmofit.app.data.repository;


public interface RepositoryCallback<T> {
    void onSuccess(T response);
    void onError(String message);
}
