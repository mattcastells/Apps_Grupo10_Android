package com.ritmofit.app.ui.classdetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.ritmofit.app.R;

public class ClassDetailFragment extends Fragment {
    
    private String disciplina;
    private String hora;
    private String profesor;
    private String cupos;
    private String duracion;
    private String sede;
    private String ubicacion;
    private String fecha;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class_detail, container, false);

        // Obtener argumentos pasados desde el fragment anterior
        Bundle args = getArguments();
        if (args != null) {
            disciplina = args.getString("disciplina", "");
            hora = args.getString("hora", "");
            profesor = args.getString("profesor", "");
            cupos = args.getString("cupos", "");
            duracion = args.getString("duracion", "");
            sede = args.getString("sede", "");
            ubicacion = args.getString("ubicacion", "");
            fecha = args.getString("fecha", "");
        }

        // Referencias a las vistas
        TextView classTitle = view.findViewById(R.id.classTitle);
        TextView classProfesor = view.findViewById(R.id.classProfesor);
        TextView classHorario = view.findViewById(R.id.classHorario);
        TextView classCupos = view.findViewById(R.id.classCupos);
        TextView classDuracion = view.findViewById(R.id.classDuracion);
        TextView classUbicacion = view.findViewById(R.id.classUbicacion);
        TextView classFecha = view.findViewById(R.id.classFecha);
        TextView classDescripcion = view.findViewById(R.id.classDescripcion);
        Button reservarButton = view.findViewById(R.id.reservarButton);
        Button backButton = view.findViewById(R.id.backButton);

        // Configurar los datos de la clase
        classTitle.setText(disciplina);
        classProfesor.setText("Profesor: " + profesor);
        classHorario.setText("Horario: " + hora);
        classCupos.setText("Cupos disponibles: " + cupos);
        classDuracion.setText("Duración: " + duracion);
        classUbicacion.setText("Ubicación: " + sede + " - " + ubicacion);
        classFecha.setText("Fecha: " + fecha);

        // Descripción mock basada en la disciplina
        String descripcion = getDescripcionPorDisciplina(disciplina);
        classDescripcion.setText(descripcion);

        // Configurar botón de reservar
        reservarButton.setOnClickListener(v -> {
            // Aquí iría la lógica para reservar la clase
            Toast.makeText(getContext(), "¡Clase reservada exitosamente!", Toast.LENGTH_SHORT).show();
            // Navegar de vuelta al home
            Navigation.findNavController(view).popBackStack();
        });

        // Configurar botón de volver
        backButton.setOnClickListener(v -> {
            Navigation.findNavController(view).popBackStack();
        });

        // Manejar el botón de atrás del sistema
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(view).popBackStack();
            }
        });

        return view;
    }

    private String getDescripcionPorDisciplina(String disciplina) {
        switch (disciplina) {
            case "Yoga":
                return "Clase de yoga que combina posturas, respiración y meditación para mejorar la flexibilidad, fuerza y bienestar mental. Ideal para todos los niveles.";
            case "Funcional":
                return "Entrenamiento funcional que utiliza movimientos naturales del cuerpo para mejorar la fuerza, resistencia y coordinación. Perfecto para quemar calorías.";
            case "Zumba":
                return "Clase de baile fitness que combina ritmos latinos con ejercicios aeróbicos. Una forma divertida de mantenerse en forma mientras bailas.";
            case "Spinning":
                return "Clase de ciclismo indoor de alta intensidad que mejora la resistencia cardiovascular y fortalece las piernas. Música motivadora incluida.";
            default:
                return "Clase de fitness diseñada para mejorar tu condición física y bienestar general. ¡Ven y disfruta de una experiencia única!";
        }
    }
}
