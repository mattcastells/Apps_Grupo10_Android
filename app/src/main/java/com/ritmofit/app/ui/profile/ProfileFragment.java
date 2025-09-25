
package com.ritmofit.app.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ritmofit.app.R;
import com.ritmofit.app.data.RitmoFitApiService;
import com.ritmofit.app.data.api.UserService;
import com.ritmofit.app.data.api.cloudinary.CloudinaryApi;
import com.ritmofit.app.data.api.cloudinary.CloudinaryUploadResponse;
import com.ritmofit.app.data.api.model.UpdatePhotoRequest;
import com.ritmofit.app.data.api.model.UserResponse;
import com.ritmofit.app.data.session.SessionManager;
import android.content.SharedPreferences;
import com.bumptech.glide.Glide;
import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private static final int PICK_IMAGE = 1001;
    private ImageView profileImage;
    private TextView nameField, emailField, ageField, genderField;
    private Button logoutButton, changePhotoButton, editInfoButton;

    private CloudinaryApi cloudinaryApi;
    private UserService userService;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = view.findViewById(R.id.profileImage);
        nameField = view.findViewById(R.id.nameField);
        emailField = view.findViewById(R.id.emailField);
        ageField = view.findViewById(R.id.ageField);
        genderField = view.findViewById(R.id.genderField);
        logoutButton = view.findViewById(R.id.logoutButton);
        changePhotoButton = view.findViewById(R.id.changePhotoButton);
        editInfoButton = view.findViewById(R.id.editInfoButton);
        editInfoButton.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(view).navigate(R.id.editUserFragment);
        });

        // Cargar información del usuario

        sessionManager = new com.ritmofit.app.data.session.SessionManager(requireContext());
        cloudinaryApi = com.ritmofit.app.data.api.cloudinary.CloudinaryService.api();
        //userService = com.ritmofit.app.data.RitmoFitApiService.getClient().create(com.ritmofit.app.data.api.UserService.class);
        userService = RitmoFitApiService.getClient(requireContext()).create(UserService.class);

        final androidx.navigation.NavController nav =
                androidx.navigation.fragment.NavHostFragment.findNavController(ProfileFragment.this);

        nav.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("refresh_profile", false)
                .observe(getViewLifecycleOwner(), should -> {
                    if (Boolean.TRUE.equals(should)) {
                        // reset para no re-disparar
                        nav.getCurrentBackStackEntry().getSavedStateHandle().set("refresh_profile", false);
                        reloadProfile(); // ← recarga
                    }
                });

        String userId = sessionManager.getUserId();
        String email  = sessionManager.getEmail();

        if (userId == null) {
            // Plan A (si JWT no tenía userId): usar email para buscarlo, o deshabilitar edición y pedir re-login
            Toast.makeText(getContext(), "No hay ID en sesión. Intenta reingresar.", Toast.LENGTH_SHORT).show();
        } else {
            // Llamar a la API de usuarios y poblar la UI
            //UserService api = RitmoFitApiService.getClient().create(UserService.class);
            UserService api = userService;
            api.getUser(userId).enqueue(new retrofit2.Callback<UserResponse>() {
                @Override public void onResponse(Call<UserResponse> call, Response<UserResponse> resp) {
                    if (resp.isSuccessful() && resp.body() != null) {
                        UserResponse u = resp.body();
                        nameField.setText(u.name != null ? u.name : "—");
                        emailField.setText(u.email != null ? u.email : "—");
                        if (u.age != null) ageField.setText(String.valueOf(u.age)); else ageField.setText("—");
                        genderField.setText(u.gender != null ? u.gender : "—");

                        if (u.profilePicture != null && !u.profilePicture.isEmpty()) {
                            Glide.with(ProfileFragment.this)
                                    .load(u.profilePicture)
                                    .placeholder(R.drawable.bodybuilder)   // usa un DRAWABLE, no un color
                                    .error(android.R.drawable.stat_notify_error)
                                    .into(profileImage);
                        }
                    } else {
                        nameField.setText("Error al cargar tu nombre de usuario.");
                        emailField.setText("Error al cargar tu email");
                        ageField.setText("Error al cargar tu edad");
                        genderField.setText("Error al cargar tu género");
                        Toast.makeText(getContext(), "No se pudo obtener la información del perfil ("+resp.code()+")", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public void onFailure(Call<UserResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de red: "+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        changePhotoButton.setOnClickListener(v -> {
            if (sessionManager.getUserId() == null) {
                Toast.makeText(getContext(), "No hay ID de usuario en sesión", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        logoutButton.setOnClickListener(v -> {
            sessionManager.clear();
            androidx.navigation.Navigation.findNavController(view).navigate(R.id.loginFragment);
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri == null) return;
            profileImage.setImageURI(imageUri);
            uploadAndPersistProfilePhoto(imageUri);
        }
    }

    private void uploadAndPersistProfilePhoto(Uri imageUri) {
        try {
            // --- Preparar multipart ---
            byte[] bytes;
            try (java.io.InputStream in = requireContext().getContentResolver().openInputStream(imageUri)) {
                if (in == null) { Toast.makeText(getContext(), "No se pudo abrir la imagen", Toast.LENGTH_SHORT).show(); return; }
                bytes = readAllBytes(in);
            }

            okhttp3.RequestBody imageBody =
                    okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/*"), bytes);
            okhttp3.MultipartBody.Part filePart =
                    okhttp3.MultipartBody.Part.createFormData("file", "profile.jpg", imageBody);

            String cloudName = requireContext().getString(R.string.cloudinary_cloud_name);
            String uploadPreset = requireContext().getString(R.string.cloudinary_upload_preset);

            okhttp3.RequestBody preset = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), uploadPreset);
            String userId = sessionManager.getUserId();
            okhttp3.RequestBody folder = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "ritmofit/users/" + (userId != null ? userId : "unknown"));
            okhttp3.RequestBody publicId = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "user_" + (userId != null ? userId : "unknown") + "_profile");

            cloudinaryApi.upload(cloudName, filePart, preset, folder, publicId)
                    .enqueue(new retrofit2.Callback<CloudinaryUploadResponse>() {
                        @Override
                        public void onResponse(retrofit2.Call<CloudinaryUploadResponse> call,
                                               retrofit2.Response<CloudinaryUploadResponse> resp) {
                            if (!resp.isSuccessful() || resp.body() == null || resp.body().secure_url == null) {
                                Toast.makeText(getContext(), "Error en la subida a Cloudinary", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String secureUrl = resp.body().secure_url;

                            // 3) Persistir en tu backend
                            persistPhotoUrlInBackend(secureUrl);
                        }

                        @Override
                        public void onFailure(retrofit2.Call<CloudinaryUploadResponse> call, Throwable t) {
                            Toast.makeText(getContext(), "Error de red (Cloudinary): " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error preparando imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static byte[] readAllBytes(java.io.InputStream is) throws java.io.IOException {
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int nRead;
        while ((nRead = is.read(data, 0, data.length)) != -1) buffer.write(data, 0, nRead);
        return buffer.toByteArray();
    }

    private void persistPhotoUrlInBackend(String secureUrl) {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            Toast.makeText(getContext(), "No hay ID de usuario en sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdatePhotoRequest body = new UpdatePhotoRequest(secureUrl);

        userService.updateUserPhoto(userId, body).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Glide.with(ProfileFragment.this)
                            .load(secureUrl)
                            .placeholder(R.drawable.bodybuilder)
                            .error(android.R.drawable.stat_notify_error)
                            .into(profileImage);
                    Toast.makeText(getContext(), "Foto actualizada", Toast.LENGTH_SHORT).show();
                } else {
                    profileImage.setImageResource(R.drawable.bodybuilder);
                    Toast.makeText(getContext(), "Error al guardar en backend: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red (backend): " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reloadProfile() {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            Toast.makeText(getContext(), "No hay ID en sesión. Intenta reingresar.", Toast.LENGTH_SHORT).show();
            return;
        }
        userService.getUser(userId).enqueue(new retrofit2.Callback<com.ritmofit.app.data.api.model.UserResponse>() {
            @Override public void onResponse(retrofit2.Call<com.ritmofit.app.data.api.model.UserResponse> call,
                                             retrofit2.Response<com.ritmofit.app.data.api.model.UserResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    com.ritmofit.app.data.api.model.UserResponse u = resp.body();
                    nameField.setText(u.name != null ? u.name : "—");
                    emailField.setText(u.email != null ? u.email : "—");
                    if (u.age != null) ageField.setText(String.valueOf(u.age)); else ageField.setText("—");
                    genderField.setText(u.gender != null ? u.gender : "—");

                    if (u.profilePicture != null && !u.profilePicture.isEmpty()) {
                        com.bumptech.glide.Glide.with(ProfileFragment.this)
                                .load(u.profilePicture)
                                .placeholder(R.drawable.bodybuilder)
                                .error(android.R.drawable.stat_notify_error)
                                .into(profileImage);
                    }
                } else {
                    Toast.makeText(getContext(), "No se pudo actualizar el perfil ("+resp.code()+")", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(retrofit2.Call<com.ritmofit.app.data.api.model.UserResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
