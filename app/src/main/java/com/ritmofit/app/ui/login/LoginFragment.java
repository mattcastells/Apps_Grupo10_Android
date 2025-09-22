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


public class LoginFragment extends Fragment {

    // Vistas de la UI
    private EditText emailEdit;
    private EditText otpEdit;
    private Button loginBtn;
    private Button verifyOtpBtn;
    private TextView errorText;

    // Services y Repositories
    private AuthRepository authRepository;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        AuthService apiService = RitmoFitApiService.getClient().create(AuthService.class);
        authRepository = new AuthRepository(apiService);

        emailEdit = view.findViewById(R.id.loginEmail);
        otpEdit = view.findViewById(R.id.loginOtp);
        loginBtn = view.findViewById(R.id.loginButton);
        verifyOtpBtn = view.findViewById(R.id.verifyOtpButton);
        errorText = view.findViewById(R.id.loginError);

        Button createUserBtn = view.findViewById(R.id.createUserButton);

        createUserBtn.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.createUserFragment);
        });
        // emailEdit.setEnabled(true);

        // Configuramos el listener del botón principal
        loginBtn.setOnClickListener(v -> handleRequestOtp());
        verifyOtpBtn.setOnClickListener(v ->  handleVerifyOtp());


        return view;
    }



    private void handleRequestOtp() {
        String email = emailEdit.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !email.contains("@")) {
            errorText.setText("Por favor, ingresa un email válido");
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        authRepository.requestOtp(email, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                Toast.makeText(getContext(), "Código enviado a tu email", Toast.LENGTH_SHORT).show();

                // Se oculta boton de login y se muestra el de verificar
                otpEdit.setVisibility(View.VISIBLE);
                verifyOtpBtn.setVisibility(View.VISIBLE);
                loginBtn.setVisibility(View.GONE);
                emailEdit.setEnabled(false);
                errorText.setVisibility(View.GONE);
            }

            @Override
            public void onError(String message) {
                errorText.setText(message);
                errorText.setVisibility(View.VISIBLE);
            }
        });
    }

    private void handleVerifyOtp() {
        String email = emailEdit.getText().toString().trim();
        String otp = otpEdit.getText().toString();

        if (TextUtils.isEmpty(otp)) {
            errorText.setText("Por favor, ingresa el código OTP");
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        authRepository.verifyOtp(email, otp, new RepositoryCallback<AuthResponse>() {
            @Override
            public void onSuccess(AuthResponse response) {
                String token = response.token();

                String userId = JwtHelper.tryExtractUserId(token);

                SessionManager sm = new SessionManager(requireContext().getApplicationContext());
                sm.saveAuth(token, userId, email);

                Toast.makeText(getContext(), "¡Login exitoso!", Toast.LENGTH_LONG).show();

                // Navegamos al Home
                if (getView() != null) {
                    Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_homeFragment);
                }
            }

            @Override
            public void onError(String message) {
                errorText.setText(message);
                errorText.setVisibility(View.VISIBLE);
            }
        });
    }
}