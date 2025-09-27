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
import com.ritmofit.app.data.session.SessionManager;
import androidx.navigation.NavOptions;
import androidx.navigation.NavController;

public class LoginFragment extends Fragment {

    private EditText emailEdit, passwordEdit;
    private Button loginBtn;
    private TextView registerText;
    private AuthRepository authRepository;
    private SessionManager sessionManager;

    // onCreateView: infla layout y configura listeners
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        AuthService apiService = RitmoFitApiService.getClient().create(AuthService.class);
        authRepository = new AuthRepository(apiService);
        sessionManager = new SessionManager(requireContext());

        emailEdit = view.findViewById(R.id.loginEmail);
        passwordEdit = view.findViewById(R.id.loginPassword);
        loginBtn = view.findViewById(R.id.loginButton);
        registerText = view.findViewById(R.id.registerText);

        loginBtn.setOnClickListener(v -> handleLogin());
        registerText.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_loginFragment_to_createUserFragment)
        );

        return view;
    }

    // handleLogin: valida credenciales y ejecuta login
    private void handleLogin() {
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString();

        if (!validateInput(email, password)) return;

        loginBtn.setEnabled(false);

        authRepository.login(email, password, new RepositoryCallback<AuthResponse>() {
            @Override
            public void onSuccess(AuthResponse response) {
                sessionManager.saveAuth(email, response.token(), response.token());
                if (!isAdded()) return;

                NavController navController = Navigation.findNavController(requireView());
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.loginFragment, true)
                        .build();
                navController.navigate(R.id.homeFragment, null, navOptions);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_LONG).show();
                loginBtn.setEnabled(true);
            }
        });
    }

    // validateInput: verifica campos no vacíos
    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailEdit.setError("El email es requerido.");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEdit.setError("La contraseña es requerida.");
            return false;
        }
        return true;
    }
}