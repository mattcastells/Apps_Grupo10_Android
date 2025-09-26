package com.ritmofit.app.ui.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.ritmofit.app.R;
import com.ritmofit.app.data.RitmoFitApiService;
import com.ritmofit.app.data.api.AuthService;
import com.ritmofit.app.data.api.model.AuthResponse;
import com.ritmofit.app.data.repository.AuthRepository;
import com.ritmofit.app.data.repository.RepositoryCallback;
import com.ritmofit.app.data.session.JwtHelper;
import com.ritmofit.app.data.session.SessionManager;


import android.content.Intent;


import androidx.navigation.NavController;
import com.ritmofit.app.ui.MainActivity;


public class LoginFragment extends Fragment {

    private EditText emailEdit, passwordEdit;
    private Button loginBtn;
    private TextView registerText;
    private AuthRepository authRepository;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        AuthService apiService = RitmoFitApiService.getClient().create(AuthService.class);
        authRepository = new AuthRepository(apiService);
        // Inicializamos SessionManager y AuthRepository
        sessionManager = new SessionManager(requireContext());
        //authRepository = new AuthRepository(sessionManager.getToken());

        // Vinculamos los componentes de la UI
        emailEdit = view.findViewById(R.id.loginEmail);
        passwordEdit = view.findViewById(R.id.loginPassword);
        loginBtn = view.findViewById(R.id.loginButton);
        registerText = view.findViewById(R.id.registerText);

        // Configuramos los listeners
        setupListeners();

        return view;
    }

    private void setupListeners() {
        loginBtn.setOnClickListener(v -> handleLogin());

        registerText.setOnClickListener(v -> {
            // Usamos NavController para ir al fragmento de registro
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_loginFragment_to_createUserFragment);
        });
    }

    private void handleLogin() {
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString();

        if (!validateInput(email, password)) {
            return;
        }

        // Deshabilitamos el botón para evitar múltiples clicks
        loginBtn.setEnabled(false);

        authRepository.login(email, password, new RepositoryCallback<AuthResponse>() {
            @Override
            public void onSuccess(AuthResponse response) {
                // En caso de éxito, guardamos el token
                sessionManager.saveAuth(email, response.token(), response.token());

                // Navegamos a la actividad principal
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                // Finalizamos la actividad actual para que el usuario no pueda volver atrás
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }

            @Override
            public void onError(String message) {
                // Mostramos el error al usuario
                Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_LONG).show();
                // Habilitamos el botón nuevamente
                loginBtn.setEnabled(true);
            }
        });
    }

    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailEdit.setError("El email es requerido.");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEdit.setError("La contraseña es requerida.");
            return false;
        }
        // Puedes añadir validaciones más complejas aquí (e.g., formato de email)
        return true;
    }
}