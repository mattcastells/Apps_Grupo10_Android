
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

public class HomeFragment extends Fragment {
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
            new ClassInfo("Central", "Spinning", "Próx. Sábado", "11:00", "Leo Díaz", "5/15", "40 min", "Salón 3"),
            new ClassInfo("Norte", "Yoga", "Mañana", "08:00", "María García", "12/15", "75 min", "Salón A"),
            new ClassInfo("Oeste", "Funcional", "Próx. Sábado", "16:00", "Carlos Mendez", "18/25", "50 min", "Box 1"),
            new ClassInfo("Central", "Zumba", "Hoy", "20:00", "Sofia Herrera", "22/25", "45 min", "Salón 2"),
            new ClassInfo("Norte", "Spinning", "Mañana", "07:00", "Diego Torres", "10/20", "40 min", "Salón B")
        };

        Runnable updateCatalog = () -> {
            classCatalogList.removeAllViews();
            String sedeSel = filterSede.getSelectedItem().toString();
            String discSel = filterDisciplina.getSelectedItem().toString();
            String fechaSel = filterFecha.getSelectedItem().toString();
            
            boolean hasResults = false;
            for (ClassInfo c : clases) {
                if ((sedeSel.equals("Todas") || c.sede.equals(sedeSel)) &&
                    (discSel.equals("Todas") || c.disciplina.equals(discSel)) &&
                    (fechaSel.equals("Todas") || c.fecha.equals(fechaSel))) {
                    
                    hasResults = true;
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

                    // Hacer la tarjeta clickeable para navegar al detalle
                    card.setOnClickListener(cardView -> {
                        Bundle args = new Bundle();
                        args.putString("disciplina", c.disciplina);
                        args.putString("hora", c.hora);
                        args.putString("profesor", c.profesor);
                        args.putString("cupos", c.cupos);
                        args.putString("duracion", c.duracion);
                        args.putString("sede", c.sede);
                        args.putString("ubicacion", c.ubicacion);
                        args.putString("fecha", c.fecha);
                        
                        Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_classDetailFragment, args);
                    });

                    classCatalogList.addView(card);
                }
            }
            
            // Mostrar mensaje si no hay resultados
            if (!hasResults) {
                TextView noResults = new TextView(requireContext());
                noResults.setText("No se encontraron clases con los filtros seleccionados");
                noResults.setTextSize(16);
                noResults.setTextColor(getResources().getColor(R.color.ritmofit_gray));
                noResults.setGravity(Gravity.CENTER);
                noResults.setPadding(24, 48, 24, 48);
                classCatalogList.addView(noResults);
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
