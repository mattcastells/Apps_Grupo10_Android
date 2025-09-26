
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
import com.ritmofit.app.data.repository.ScheduleRepository;
import com.ritmofit.app.data.api.model.ScheduledClassDto;

public class HomeFragment extends Fragment {
    
    private ScheduleRepository scheduleRepository;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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

        // Catálogo de Clases y Turnos (mockeado hasta conectar el back)
        Spinner filterSede = view.findViewById(R.id.filterSede);
        Spinner filterDisciplina = view.findViewById(R.id.filterDisciplina);
        Spinner filterFecha = view.findViewById(R.id.filterFecha);
        LinearLayout classCatalogList = view.findViewById(R.id.classCatalogList);

        String[] sedes = {"Todas", "Central", "Norte", "Oeste"};
        String[] disciplinas = {"Todas", "Yoga", "Funcional", "Zumba", "Spinning"};
        String[] fechas = {"Hoy", "Mañana", "Próx. Sábado"};

        ArrayAdapter<String> sedeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, sedes);
        sedeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSede.setAdapter(sedeAdapter);

        ArrayAdapter<String> discAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, disciplinas);
        discAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterDisciplina.setAdapter(discAdapter);

        ArrayAdapter<String> fechaAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, fechas);
        fechaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterFecha.setAdapter(fechaAdapter);

        // Mock de clases
        class ClassInfo {
            String sede, disciplina, fecha, hora, profesor, cupos, duracion, ubicacion;
            ClassInfo(String sede, String disciplina, String fecha, String hora, String profesor, String cupos, String duracion, String ubicacion) {
                this.sede = sede; this.disciplina = disciplina; this.fecha = fecha; this.hora = hora;
                this.profesor = profesor; this.cupos = cupos; this.duracion = duracion; this.ubicacion = ubicacion;
            }
        }
        ClassInfo[] clases = {
            new ClassInfo("Central", "Yoga", "Hoy", "10:00", "Ana López", "8/20", "60 min", "Salón 1"),
            new ClassInfo("Norte", "Funcional", "Hoy", "18:00", "Juan Pérez", "15/20", "45 min", "Box 2"),
            new ClassInfo("Oeste", "Zumba", "Mañana", "19:00", "Carla Ruiz", "20/20", "50 min", "Salón 2"),
            new ClassInfo("Central", "Spinning", "Próx. Sábado", "11:00", "Leo Díaz", "5/15", "40 min", "Salón 3")
        };

        Runnable updateCatalog = () -> {
            classCatalogList.removeAllViews();
            String sedeSel = filterSede.getSelectedItem().toString();
            String discSel = filterDisciplina.getSelectedItem().toString();
            String fechaSel = filterFecha.getSelectedItem().toString();
            for (ClassInfo c : clases) {
                if ((sedeSel.equals("Todas") || c.sede.equals(sedeSel)) &&
                    (discSel.equals("Todas") || c.disciplina.equals(discSel)) &&
                    (fechaSel.equals("Hoy") && c.fecha.equals("Hoy") ||
                     fechaSel.equals("Mañana") && c.fecha.equals("Mañana") ||
                     fechaSel.equals("Próx. Sábado") && c.fecha.equals("Próx. Sábado"))) {
                    LinearLayout card = new LinearLayout(requireContext());
                    card.setOrientation(LinearLayout.VERTICAL);
                    card.setBackgroundResource(R.drawable.class_card_bg);
                    card.setElevation(8f);
                    card.setPadding(24, 20, 24, 20);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, 24);
                    card.setLayoutParams(params);
                    
                    card.setClickable(true);
                    card.setFocusable(true);

                    TextView title = new TextView(requireContext());
                    title.setText(c.disciplina + " - " + c.hora);
                    title.setTextSize(19);
                    title.setTextColor(getResources().getColor(R.color.ritmofit_orange));
                    title.setTypeface(null, android.graphics.Typeface.BOLD);
                    card.addView(title);

                    TextView prof = new TextView(requireContext());
                    prof.setText("Profesor: " + c.profesor);
                    prof.setTextSize(16);
                    card.addView(prof);

                    TextView cupos = new TextView(requireContext());
                    cupos.setText("Cupos: " + c.cupos);
                    cupos.setTextSize(16);
                    card.addView(cupos);

                    TextView dur = new TextView(requireContext());
                    dur.setText("Duración: " + c.duracion);
                    dur.setTextSize(16);
                    card.addView(dur);

                    TextView sede = new TextView(requireContext());
                    sede.setText("Sede: " + c.sede + " - " + c.ubicacion);
                    sede.setTextSize(16);
                    card.addView(sede);

                    // Click listener para navegar a detalles
                    card.setOnClickListener(v -> {
                        Bundle args = new Bundle();
                        args.putString("classId", "class_" + c.disciplina + "_" + c.hora);
                        args.putString("className", c.disciplina);
                        args.putString("classTime", c.hora);
                        args.putString("classDate", c.fecha);
                        args.putString("classProfessor", c.profesor);
                        args.putString("classDuration", c.duracion);
                        args.putString("classCapacity", c.cupos);
                        args.putString("classLocation", c.sede + " - " + c.ubicacion);
                        args.putString("classDescription", "Clase de " + c.disciplina + " con " + c.profesor + ". " + c.duracion + " de duración.");
                        
                        Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_classDetailFragment, args);
                    });

                    classCatalogList.addView(card);
                }
            }
        };

        filterSede.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View v, int pos, long id) { updateCatalog.run(); }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        filterDisciplina.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View v, int pos, long id) { updateCatalog.run(); }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        filterFecha.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View v, int pos, long id) { updateCatalog.run(); }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        updateCatalog.run();

        return view;
    }
}
