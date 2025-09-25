package com.ritmofit.app.data.api.cloudinary;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class CloudinaryService {
    private static Retrofit retrofit;
    public static CloudinaryApi api() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.cloudinary.com/") // base común
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(CloudinaryApi.class);
    }
}