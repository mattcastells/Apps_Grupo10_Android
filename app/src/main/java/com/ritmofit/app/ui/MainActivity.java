package com.ritmofit.app.ui;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.NavDestination;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ritmofit.app.R;
import com.ritmofit.app.data.session.SessionManager;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private NavController navController;

    // onCreate: configura navegación y visibilidad del bottom nav
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNav);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host);
        if (navHostFragment == null) return;
        navController = navHostFragment.getNavController();

        SessionManager sessionManager = new SessionManager(this);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            boolean isAuth = destination.getId() == R.id.loginFragment
                    || destination.getId() == R.id.createUserFragment;
            if (isAuth) {
                bottomNav.setVisibility(View.GONE);
                if (getSupportActionBar() != null) getSupportActionBar().hide();
            } else {
                bottomNav.setVisibility(View.VISIBLE);
                if (getSupportActionBar() != null) getSupportActionBar().show();
            }
        });

        if (sessionManager.getToken() == null) {
            bottomNav.setVisibility(View.GONE);
            if (getSupportActionBar() != null) getSupportActionBar().hide();
            bottomNav.post(() -> {
                if (navController.getCurrentDestination() != null &&
                        navController.getCurrentDestination().getId() != R.id.loginFragment) {
                    navController.navigate(R.id.loginFragment);
                }
            });
        }

        bottomNav.setOnItemReselectedListener(item -> { });
        bottomNav.setOnItemSelectedListener(item -> {
            int dest = item.getItemId();
            NavDestination current = navController.getCurrentDestination();
            if (current != null && current.getId() == dest) return true;
            navController.navigate(dest);
            return true;
        });
    }

    // onSupportNavigateUp: delega navegación hacia atrás
    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
