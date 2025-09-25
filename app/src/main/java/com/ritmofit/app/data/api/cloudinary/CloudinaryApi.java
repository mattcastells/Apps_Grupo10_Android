package com.ritmofit.app.data.api.cloudinary;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface CloudinaryApi {
    @Multipart
    @POST("v1_1/{cloud}/image/upload")
    Call<CloudinaryUploadResponse> upload(
            @Path("cloud") String cloud,
            @Part MultipartBody.Part file,
            @Part("upload_preset") RequestBody uploadPreset,
            @Part("folder") RequestBody folder,
            @Part("public_id") RequestBody publicId
    );
}