package com.ritmofit.app.ui.otp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.ritmofit.app.R;
import com.ritmofit.app.data.RitmoFitApiService;
import com.ritmofit.app.data.api.AuthService;
import com.ritmofit.app.data.repository.AuthRepository;
import com.ritmofit.app.data.repository.RepositoryCallback;
import com.ritmofit.app.data.session.SessionManager;
import java.util.Map;

public class OtpVerificationFragment extends Fragment {

    private EditText otpEdit;
    private Button verifyOtpButton;
    private AuthRepository authRepository;
    private String userEmail;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Recibimos el email que pasamos desde el fragment de registro
        if (getArguments() != null) {
            userEmail = getArguments().getString("email");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_otp_verify, container, false);

        SessionManager sessionManager = new SessionManager(requireContext());
        AuthService apiService = RitmoFitApiService.getClient().create(AuthService.class);
        authRepository = new AuthRepository(apiService);

        otpEdit = view.findViewById(R.id.otpEdit);
        verifyOtpButton = view.findViewById(R.id.verifyOtpButton);

        verifyOtpButton.setOnClickListener(v -> handleVerification());

        return view;
    }

    private void handleVerification() {
        String otp = otpEdit.getText().toString().trim();

        if (TextUtils.isEmpty(otp) || otp.length() != 6) {
            otpEdit.setError("Ingresa un código OTP válido de 6 dígitos.");
            return;
        }

        verifyOtpButton.setEnabled(false);

        authRepository.verifyEmail(userEmail, otp, new RepositoryCallback<Map<String, String>>() {
            @Override
            public void onSuccess(Map<String, String> response) {
                Toast.makeText(getContext(), "¡Email verificado con éxito! Por favor, inicia sesión.", Toast.LENGTH_LONG).show();
                // Navegamos de vuelta al login para que el usuario pueda ingresar
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_otpVerificationFragment_to_loginFragment);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_LONG).show();
                verifyOtpButton.setEnabled(true);
            }
        });
    }
}