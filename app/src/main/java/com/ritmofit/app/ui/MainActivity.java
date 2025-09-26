package com.ritmofit.app.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ritmofit.app.R;
import com.ritmofit.app.data.session.SessionManager;

public class MainActivity extends AppCompatActivity {
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SessionManager sessionManager = new SessionManager(this);

        NavHostFragment navHost =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host);
        if (navHost == null) throw new IllegalStateException("NavHost not found");
        navController = navHost.getNavController();

        if (sessionManager.getToken() == null) {
            // No hay token, redirigir a Login
            navController.navigate(R.id.action_global_loginFragment);
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        NavigationUI.setupWithNavController(bottomNav, navController);
        bottomNav.setOnItemReselectedListener(item -> { });

        bottomNav.setOnItemSelectedListener(item -> {
            int dest = item.getItemId();
            if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == dest) {
                return true;
            }
            navController.popBackStack(navController.getGraph().getStartDestinationId(), false);
            navController.navigate(dest);
            return true;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && navController.navigateUp() || super.onSupportNavigateUp();
    }
}
