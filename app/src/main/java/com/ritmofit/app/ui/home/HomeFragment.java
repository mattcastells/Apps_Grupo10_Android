
package com.ritmofit.app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Gravity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.ritmofit.app.R;
import com.ritmofit.app.data.RitmoFitApiService;
import com.ritmofit.app.data.api.ScheduleService;
import com.ritmofit.app.data.api.model.ScheduledClassDto;
import com.ritmofit.app.data.repository.RepositoryCallback;
import com.ritmofit.app.data.repository.ScheduleRepository;
import java.util.List;
import java.util.ArrayList;

public class HomeFragment extends Fragment {
    
    private ScheduleRepository scheduleRepository;
    private List<ScheduledClassDto> allClasses = new ArrayList<>();
    private LinearLayout classCatalogList;
    private Spinner filterSede, filterDisciplina, filterFecha;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inicializar repositorio
        ScheduleService scheduleService = RitmoFitApiService.getClient(getContext()).create(ScheduleService.class);
        scheduleRepository = new ScheduleRepository(scheduleService);

        Button goToReservationsButton = view.findViewById(R.id.goToReservationsButton);
        Button goToProfileButton = view.findViewById(R.id.goToProfileButton);

        goToReservationsButton.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.reservationsFragment);
        });
        goToProfileButton.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.profileFragment);
        });

        // Esto lo usamos para limpiar el backstack asi la app no queda atrapada en un fragment
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(view).popBackStack(R.id.homeFragment, false);
            }
        });

        // Catálogo de Clases y Turnos (ahora desde el backend)
        filterSede = view.findViewById(R.id.filterSede);
        filterDisciplina = view.findViewById(R.id.filterDisciplina);
        filterFecha = view.findViewById(R.id.filterFecha);
        classCatalogList = view.findViewById(R.id.classCatalogList);

        String[] sedes = {"Todas", "Central", "Norte", "Oeste"};
        String[] disciplinas = {"Todas", "Yoga", "Funcional", "Zumba", "Spinning"};
        String[] fechas = {"Todas", "Hoy", "Mañana", "Próx. Sábado"};

        ArrayAdapter<String> sedeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, sedes);
        sedeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSede.setAdapter(sedeAdapter);

        ArrayAdapter<String> discAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, disciplinas);
        discAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterDisciplina.setAdapter(discAdapter);

        ArrayAdapter<String> fechaAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, fechas);
        fechaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterFecha.setAdapter(fechaAdapter);

        // Cargar clases desde el backend
        loadClassesFromBackend();

        // Configurar listeners para filtros
        filterDisciplina.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View v, int pos, long id) { 
                updateCatalogDisplay(); 
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        return view;
    }

    private void loadClassesFromBackend() {
        scheduleRepository.getWeeklySchedule(new RepositoryCallback<List<ScheduledClassDto>>() {
            @Override
            public void onSuccess(List<ScheduledClassDto> classes) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        allClasses.clear();
                        allClasses.addAll(classes);
                        updateCatalogDisplay();
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error al cargar clases: " + error, Toast.LENGTH_LONG).show();
                        showNoResultsMessage("Error al cargar las clases del servidor");
                    });
                }
            }
        });
    }

    private void updateCatalogDisplay() {
        classCatalogList.removeAllViews();
        
        String discSel = filterDisciplina.getSelectedItem() != null ? 
            filterDisciplina.getSelectedItem().toString() : "Todas";
        
        List<ScheduledClassDto> filteredClasses = new ArrayList<>();
        
        for (ScheduledClassDto scheduledClass : allClasses) {
            if (discSel.equals("Todas") || scheduledClass.getName().contains(discSel)) {
                filteredClasses.add(scheduledClass);
            }
        }
        
        if (filteredClasses.isEmpty()) {
            showNoResultsMessage("No se encontraron clases con los filtros seleccionados");
            return;
        }
        
        for (ScheduledClassDto scheduledClass : filteredClasses) {
            createClassCard(scheduledClass);
        }
    }

    private void createClassCard(ScheduledClassDto scheduledClass) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.class_card_bg);
        card.setElevation(8f);
        card.setPadding(24, 20, 24, 20);
        card.setClickable(true);
        card.setFocusable(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 24);
        card.setLayoutParams(params);

        TextView title = new TextView(requireContext());
        title.setText(scheduledClass.getName() + " - " + formatTime(scheduledClass.getDateTime()));
        title.setTextSize(19);
        title.setTextColor(getResources().getColor(R.color.ritmofit_orange));
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        card.addView(title);

        TextView prof = new TextView(requireContext());
        prof.setText("Profesor: " + scheduledClass.getProfessor());
        prof.setTextSize(16);
        card.addView(prof);

        TextView cupos = new TextView(requireContext());
        cupos.setText("Cupos disponibles: " + scheduledClass.getAvailableSlots());
        cupos.setTextSize(16);
        card.addView(cupos);

        TextView dur = new TextView(requireContext());
        dur.setText("Duración: " + scheduledClass.getDurationMinutes() + " min");
        dur.setTextSize(16);
        card.addView(dur);

        TextView fecha = new TextView(requireContext());
        fecha.setText("Fecha: " + formatDate(scheduledClass.getDateTime()));
        fecha.setTextSize(16);
        card.addView(fecha);

        // Hacer la tarjeta clickeable para navegar al detalle
        card.setOnClickListener(cardView -> {
            Bundle args = new Bundle();
            args.putString("classId", scheduledClass.getId());
            
            Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_classDetailFragment, args);
        });

        classCatalogList.addView(card);
    }

    private void showNoResultsMessage(String message) {
        TextView noResults = new TextView(requireContext());
        noResults.setText(message);
        noResults.setTextSize(16);
        noResults.setTextColor(getResources().getColor(R.color.ritmofit_gray));
        noResults.setGravity(Gravity.CENTER);
        noResults.setPadding(24, 48, 24, 48);
        classCatalogList.addView(noResults);
    }

    private String formatTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.length() < 16) return "N/A";
        return dateTimeString.substring(11, 16);
    }

    private String formatDate(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.length() < 10) return "N/A";
        return dateTimeString.substring(0, 10);
    }
}
