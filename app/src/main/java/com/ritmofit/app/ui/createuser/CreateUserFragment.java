package com.ritmofit.app.ui.createuser;

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
import com.ritmofit.app.data.api.AuthService;
import com.ritmofit.app.data.api.UserService;
import com.ritmofit.app.data.api.model.UserRequest;
import com.ritmofit.app.data.repository.AuthRepository;
import com.ritmofit.app.data.repository.RepositoryCallback;
import com.ritmofit.app.data.repository.UserRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreateUserFragment extends Fragment {
    private static final int PICK_IMAGE = 1;
    private ImageView profileImage;
    private EditText nameEditText, emailEditText, ageEditText, passwordEditText, confirmPasswordEditText;
    private Spinner genderSpinner;
    private Button createUserButton, changePhotoButton;
    private Uri selectedImageUri;

    private UserRepository userRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_user, container, false);

        profileImage = view.findViewById(R.id.profileImage);
        nameEditText = view.findViewById(R.id.nameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        ageEditText = view.findViewById(R.id.ageEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);
        genderSpinner = view.findViewById(R.id.genderSpinner);
        createUserButton = view.findViewById(R.id.createUserButton);
        changePhotoButton = view.findViewById(R.id.changePhotoButton);

        // Gender spinner
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gender_options, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Services
        UserService apiService = RitmoFitApiService.getClient().create(UserService.class);
        userRepository = new UserRepository(apiService);

        changePhotoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        createUserButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String ageStr = ageEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();
            String gender = genderSpinner.getSelectedItem().toString();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(ageStr) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(getContext(), "Por favor completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            userRepository.createUser(name, email, ageStr, gender, password, new RepositoryCallback<Void>() {
                @Override
                public void onSuccess(Void data) {
                    Toast.makeText(getContext(), "Usuario creado exitosamente", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onError(String message) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        });

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
}
