
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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ritmofit.app.R;
import com.ritmofit.app.data.RitmoFitApiService;
import com.ritmofit.app.data.api.UserService;
import com.ritmofit.app.data.api.model.UserResponse;
import com.ritmofit.app.data.session.SessionManager;

import retrofit2.Call;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private static final int PICK_IMAGE = 1;
    private ImageView profileImage;
    private EditText nameEditText, emailEditText;
    private Button saveButton, logoutButton, changePhotoButton, editInfoButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = view.findViewById(R.id.profileImage);
        nameEditText = view.findViewById(R.id.nameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        saveButton = view.findViewById(R.id.saveButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        changePhotoButton = view.findViewById(R.id.changePhotoButton);
        editInfoButton = view.findViewById(R.id.editInfoButton);
        editInfoButton.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(view).navigate(R.id.editUserFragment);
        });

        // Cargar información del usuario
        SessionManager sm = new SessionManager(requireContext().getApplicationContext());
        String userId = sm.getUserId();
        String email  = sm.getEmail(); // útil como fallback

        if (userId == null) {
            // Plan A (si JWT no tenía userId): usar email para buscarlo, o deshabilitar edición y pedir re-login
            Toast.makeText(getContext(), "No hay ID en sesión. Intenta reingresar.", Toast.LENGTH_SHORT).show();
        } else {
            // Llamar a la API de usuarios y poblar la UI
            UserService api = RitmoFitApiService.getClient().create(UserService.class);
            api.getUser(userId).enqueue(new retrofit2.Callback<UserResponse>() {
                @Override public void onResponse(Call<UserResponse> call, Response<UserResponse> resp) {
                    if (resp.isSuccessful() && resp.body() != null) {
                        UserResponse u = resp.body();
                        nameEditText.setText(u.name);
                        emailEditText.setText(u.email);
                        // profileImage: si tenés URL de foto (u.profilePicture), cargala con Glide/Picasso
                    } else {
                        nameEditText.setText("Nombre Apellido");
                        emailEditText.setText("Dirección de email");
                        Toast.makeText(getContext(), "No se pudo obtener la información del perfil ("+resp.code()+")", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public void onFailure(Call<UserResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de red: "+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        changePhotoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        saveButton.setOnClickListener(v -> {
            // Guardar cambios (implementar lógica real)
            Toast.makeText(getContext(), "Cambios guardados", Toast.LENGTH_SHORT).show();
        });

        logoutButton.setOnClickListener(v -> {
            // Navegar a LoginFragment
            androidx.navigation.Navigation.findNavController(view).navigate(R.id.loginFragment);
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
    }
}
