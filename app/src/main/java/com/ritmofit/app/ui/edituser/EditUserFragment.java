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
import com.ritmofit.app.network.ApiClient;
import com.ritmofit.app.network.api.UserApi;
import com.ritmofit.app.network.request.UserRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserFragment extends Fragment {
    private static final int PICK_IMAGE = 1;
    private ImageView profileImage;
    private EditText nameEditText, emailEditText, ageEditText, classTypesEditText, passwordEditText, confirmPasswordEditText;
    private Spinner genderSpinner, roleSpinner;
    private Button saveEditButton, changePhotoButton;
    private LinearLayout studentFields, professorFields;
    private Uri selectedImageUri;
    UserApi api = ApiClient.getClient().create(UserApi.class);

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
        roleSpinner = view.findViewById(R.id.roleSpinner);
        saveEditButton = view.findViewById(R.id.saveEditButton);
        changePhotoButton = view.findViewById(R.id.changePhotoButton);
        studentFields = view.findViewById(R.id.studentFields);
        professorFields = view.findViewById(R.id.professorFields);
        classTypesEditText = view.findViewById(R.id.classTypesEditText);

        // Gender spinner
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gender_options, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Role spinner
        ArrayAdapter<CharSequence> roleAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.role_options, android.R.layout.simple_spinner_item);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(roleAdapter);

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String role = (String) parent.getItemAtPosition(position);
                if (role.equals("Estudiante")) {
                    studentFields.setVisibility(View.VISIBLE);
                    professorFields.setVisibility(View.GONE);
                } else if (role.equals("Profesor")) {
                    studentFields.setVisibility(View.GONE);
                    professorFields.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        changePhotoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        saveEditButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String ageStr = ageEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();
            String gender = genderSpinner.getSelectedItem().toString();
            String role = roleSpinner.getSelectedItem().toString();
            String classTypes = classTypesEditText.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(ageStr) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(getContext(), "Por favor completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: Obtener el ID real del usuario logueado
            String userId = "ID_DEL_USUARIO";
            UserRequest req = new UserRequest();
            req.name = name;
            req.email = email;
            req.age = Integer.valueOf(ageStr);
            req.gender = gender;
            req.profilePicture = null;
            req.password = password;
            api.updateUser(userId, req).enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Cambios guardados correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error al guardar cambios", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
