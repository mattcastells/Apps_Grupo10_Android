package com.ritmofit.app.ui.edituser;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ritmofit.app.R;
import com.ritmofit.app.data.RitmoFitApiService;
import com.ritmofit.app.data.api.UserService;
import com.ritmofit.app.data.api.cloudinary.CloudinaryApi;
import com.ritmofit.app.data.api.cloudinary.CloudinaryService;
import com.ritmofit.app.data.api.model.UserResponse;
import com.ritmofit.app.data.repository.RepositoryCallback;
import com.ritmofit.app.data.repository.UserRepository;
import com.ritmofit.app.data.session.SessionManager;

import retrofit2.Call;
import retrofit2.Response;
import android.util.Patterns;


public class EditUserFragment extends Fragment {
    private static final int PICK_IMAGE = 1;
    private ImageView profileImage;
    private EditText nameEditText, emailEditText, ageEditText, passwordEditText, confirmPasswordEditText;
    private Spinner genderSpinner;
    private Button saveEditButton, changePhotoButton, cancelEditButton;
    private Uri selectedImageUri;

    // Services
    private UserRepository userRepository;
    private UserService userService;
    private SessionManager sessionManager;
    private CloudinaryApi cloudinaryApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_user, container, false);

        profileImage = view.findViewById(R.id.profileImage);
        nameEditText = view.findViewById(R.id.nameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        ageEditText = view.findViewById(R.id.ageEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);
        genderSpinner = view.findViewById(R.id.genderSpinner);
        saveEditButton = view.findViewById(R.id.saveEditButton);
        changePhotoButton = view.findViewById(R.id.changePhotoButton);
        cancelEditButton = view.findViewById(R.id.cancelEditButton);

        changePhotoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        cancelEditButton.setOnClickListener(v ->
                androidx.navigation.fragment.NavHostFragment
                        .findNavController(EditUserFragment.this)
                        .navigateUp()
        );

        // Services
        //UserService apiService = RitmoFitApiService.getClient().create(UserService.class);
        //userRepository = new UserRepository(apiService);
        // Services
        userService   = RitmoFitApiService.getClient(requireContext()).create(UserService.class);
        userRepository= new UserRepository(userService);
        sessionManager= new SessionManager(requireContext());
        cloudinaryApi = CloudinaryService.api();


        saveEditButton.setOnClickListener(v -> {
            String name   = nameEditText.getText().toString().trim();
            String email  = emailEditText.getText().toString().trim();
            String ageStr = ageEditText.getText().toString().trim();
            String gender = (genderSpinner.getSelectedItem() != null)
                    ? genderSpinner.getSelectedItem().toString()
                    : null;

            final String userId = sessionManager.getUserId();
            if (userId == null) { Toast.makeText(getContext(), "No hay ID en sesión. Reingresá.", Toast.LENGTH_SHORT).show(); return; }
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(ageStr) || TextUtils.isEmpty(gender)) {
                Toast.makeText(getContext(), "Completá nombre, email, edad y género", Toast.LENGTH_SHORT).show(); return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(getContext(), "Email inválido", Toast.LENGTH_SHORT).show();
                return;
            }
            int age;
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Edad debe ser numérica", Toast.LENGTH_SHORT).show();
                return;
            }
            if (age < 13 || age > 120) {
                Toast.makeText(getContext(), "Edad fuera de rango (13–120)", Toast.LENGTH_SHORT).show();
                return;
            }

            String passInput = passwordEditText.getText().toString();
            String confInput = confirmPasswordEditText.getText().toString();
            boolean wantsPwdChange = !TextUtils.isEmpty(passInput) || !TextUtils.isEmpty(confInput);
            if (wantsPwdChange) {
                if (TextUtils.isEmpty(passInput) || TextUtils.isEmpty(confInput)) {
                    Toast.makeText(getContext(), "Completá ambas contraseñas", Toast.LENGTH_SHORT).show(); return;
                }
                if (!passInput.equals(confInput)) {
                    Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show(); return;
                }
            }
            final String passwordToSend = wantsPwdChange ? passInput : null;

            setSaving(true);

            if (selectedImageUri != null) {
                uploadToCloudinary(userId, selectedImageUri, new RepositoryCallback<String>() {
                    @Override public void onSuccess(String secureUrl) {
                        persistPhotoUrlInBackend(userId, secureUrl, new RepositoryCallback<Void>() {
                            @Override public void onSuccess(Void ignore) {
                                updateUserData(userId, name, email, ageStr, gender, passwordToSend);
                            }
                            @Override public void onError(String msg) { Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show(); }
                        });
                    }
                    @Override public void onError(String msg) { Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show(); }
                });
            } else {
                updateUserData(userId, name, email, ageStr, gender, passwordToSend);
            }
        });

        preloadUser();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            profileImage.setImageURI(selectedImageUri);
        }
    }

    private void uploadToCloudinary(String userId, Uri imageUri, RepositoryCallback<String> cb) {
        try {
            byte[] bytes;
            try (java.io.InputStream in = requireContext().getContentResolver().openInputStream(imageUri)) {
                if (in == null) {
                    setSaving(false);
                    cb.onError("No se pudo abrir la imagen.");
                    return;
                }
                bytes = readAllBytes(in);
            }

            okhttp3.RequestBody imageBody =
                    okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/*"), bytes);
            okhttp3.MultipartBody.Part filePart =
                    okhttp3.MultipartBody.Part.createFormData("file", "profile.jpg", imageBody);

            String cloudName    = requireContext().getString(R.string.cloudinary_cloud_name);
            String uploadPreset = requireContext().getString(R.string.cloudinary_upload_preset);

            okhttp3.RequestBody preset   = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), uploadPreset);
            okhttp3.RequestBody folder   = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "ritmofit/users/" + (userId != null ? userId : "unknown"));
            okhttp3.RequestBody publicId = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "user_" + (userId != null ? userId : "unknown") + "_profile");

            cloudinaryApi.upload(cloudName, filePart, preset, folder, publicId)
                    .enqueue(new retrofit2.Callback<com.ritmofit.app.data.api.cloudinary.CloudinaryUploadResponse>() {
                        @Override public void onResponse(retrofit2.Call<com.ritmofit.app.data.api.cloudinary.CloudinaryUploadResponse> call,
                                                         retrofit2.Response<com.ritmofit.app.data.api.cloudinary.CloudinaryUploadResponse> resp) {
                            if (!resp.isSuccessful() || resp.body() == null || resp.body().secure_url == null) {
                                setSaving(false);
                                cb.onError("Error en la subida a Cloudinary");
                                return;
                            }
                            cb.onSuccess(resp.body().secure_url);
                        }
                        @Override public void onFailure(retrofit2.Call<com.ritmofit.app.data.api.cloudinary.CloudinaryUploadResponse> call, Throwable t) {
                            setSaving(false);
                            cb.onError("Error de red (Cloudinary): " + t.getMessage());
                        }
                    });

        } catch (Exception e) {
            setSaving(false);
            cb.onError("Error preparando imagen: " + e.getMessage());
        }
    }

    private void persistPhotoUrlInBackend(String userId, String secureUrl, RepositoryCallback<Void> cb) {
        if (userId == null) {
            setSaving(false);
            cb.onError("No hay ID de usuario en sesión");
            return;
        }
        com.ritmofit.app.data.api.model.UpdatePhotoRequest body =
                new com.ritmofit.app.data.api.model.UpdatePhotoRequest(secureUrl);

        userService.updateUserPhoto(userId, body).enqueue(new retrofit2.Callback<Void>() {
            @Override public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> resp) {
                if (resp.isSuccessful()) {
                    com.bumptech.glide.Glide.with(EditUserFragment.this)
                            .load(secureUrl)
                            .placeholder(R.drawable.bodybuilder)
                            .error(android.R.drawable.stat_notify_error)
                            .into(profileImage);
                    cb.onSuccess(null);
                } else {
                    setSaving(false);
                    cb.onError("Error al guardar foto en backend: " + resp.code());
                }
            }
            @Override public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                setSaving(false);
                cb.onError("Error de red (backend): " + t.getMessage());
            }
        });
    }


    private void updateUserData(String userId, String name, String email, String ageStr, String gender, String password) {
        userRepository.updateUser(userId, name, email, ageStr, gender, /*profilePicture*/ null, password,
                new RepositoryCallback<Void>() {
                    @Override public void onSuccess(Void ignore) {
                        setSaving(false);
                        passwordEditText.setText("");
                        confirmPasswordEditText.setText("");
                        selectedImageUri = null;
                        Toast.makeText(getContext(), "Cambios guardados", Toast.LENGTH_SHORT).show();

                        // antes de volver, avisamos al fragment anterior que refresque
                        androidx.navigation.NavController nav = androidx.navigation.fragment.NavHostFragment.findNavController(EditUserFragment.this);
                        nav.getPreviousBackStackEntry().getSavedStateHandle().set("refresh_profile", true);
                        nav.navigateUp(); // volver atrás
                    }
                    @Override public void onError(String msg) {
                        setSaving(false);
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static byte[] readAllBytes(java.io.InputStream is) throws java.io.IOException {
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        byte[] data = new byte[8192]; int n;
        while ((n = is.read(data, 0, data.length)) != -1) buf.write(data, 0, n);
        return buf.toByteArray();
    }

    private void preloadUser() {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            Toast.makeText(getContext(), "No hay ID en sesión. Reingresá.", Toast.LENGTH_SHORT).show();
            return;
        }

        userService.getUser(userId).enqueue(new retrofit2.Callback<UserResponse>() {
            @Override public void onResponse(Call<UserResponse> call, Response<UserResponse> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(getContext(), "No se pudo cargar el perfil ("+resp.code()+")", Toast.LENGTH_SHORT).show();
                    return;
                }
                UserResponse u = resp.body();
                nameEditText.setText(u.name != null ? u.name : "");
                emailEditText.setText(u.email != null ? u.email : "");
                ageEditText.setText(u.age != null ? String.valueOf(u.age) : "");
                selectGender(u.gender); // Seleccionar género en el Spinner
                if (u.profilePicture != null && !u.profilePicture.isEmpty()) {
                    com.bumptech.glide.Glide.with(EditUserFragment.this)
                            .load(u.profilePicture)
                            .placeholder(R.drawable.bodybuilder)
                            .error(android.R.drawable.stat_notify_error)
                            .into(profileImage);
                }
            }
            @Override public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectGender(String gender) {
        if (gender == null) return;
        android.widget.SpinnerAdapter adapter = genderSpinner.getAdapter();
        if (adapter == null) return;
        for (int i = 0; i < adapter.getCount(); i++) {
            Object item = adapter.getItem(i);
            if (item != null && gender.equalsIgnoreCase(item.toString())) {
                genderSpinner.setSelection(i, false);
                return;
            }
        }
    }

    private void setSaving(boolean saving) {
        saveEditButton.setEnabled(!saving);
        saveEditButton.setText(saving ? "Guardando…" : "Guardar cambios");
    }

}
