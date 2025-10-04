package com.ritmofit.app.ui.classdetail;

import android.content.Intent;
import android.net.Uri;
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
import com.ritmofit.app.data.RitmoFitApiService;
import com.ritmofit.app.data.api.BookingService;
import com.ritmofit.app.data.api.ScheduleService;
import com.ritmofit.app.data.api.model.BookingResponse;
import com.ritmofit.app.data.api.model.ScheduledClassDto;
import com.ritmofit.app.data.repository.BookingRepository;
import com.ritmofit.app.data.repository.RepositoryCallback;
import com.ritmofit.app.data.repository.ScheduleRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ClassDetailFragment extends Fragment {
    
    private String disciplina;
    private String hora;
    private String profesor;
    private String cupos;
    private String duracion;
    private String sede;
    private String ubicacion;
    private String fecha;
    
    // Variables para integración con backend
    private String classId = "";
    private ScheduleRepository scheduleRepository;
    private BookingRepository bookingRepository;
    
    // Referencias a las vistas
    private TextView classTitle;
    private TextView classProfesor;
    private TextView classHorario;
    private TextView classCupos;
    private TextView classDuracion;
    private TextView classUbicacion;
    private TextView classFecha;
    private TextView classDescripcion;
    private Button reservarButton;
    private Button backButton;
    private Button verMapaButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class_detail, container, false);

        // Inicializar repositorios
        ScheduleService scheduleService = RitmoFitApiService.getClient(getContext()).create(ScheduleService.class);
        scheduleRepository = new ScheduleRepository(scheduleService);
        
        BookingService bookingService = RitmoFitApiService.getClient(getContext()).create(BookingService.class);
        bookingRepository = new BookingRepository(bookingService);

        // Referencias a las vistas
        classTitle = view.findViewById(R.id.classTitle);
        classProfesor = view.findViewById(R.id.classProfesor);
        classHorario = view.findViewById(R.id.classHorario);
        classCupos = view.findViewById(R.id.classCupos);
        classDuracion = view.findViewById(R.id.classDuracion);
        classUbicacion = view.findViewById(R.id.classUbicacion);
        classFecha = view.findViewById(R.id.classFecha);
        classDescripcion = view.findViewById(R.id.classDescripcion);
        reservarButton = view.findViewById(R.id.reservarButton);
        backButton = view.findViewById(R.id.backButton);
        verMapaButton = view.findViewById(R.id.verMapaButton);

        // Obtener argumentos pasados desde el fragment anterior
        Bundle args = getArguments();
        if (args != null) {
            // Verificar si se pasó un ID de clase (nuevo flujo con backend)
            classId = args.getString("classId", "");
            
            if (!classId.isEmpty()) {
                // Nuevo flujo: obtener datos del backend
                loadClassDetailFromBackend(classId);
            } else {
                // Flujo legacy: usar datos pasados como argumentos
                loadClassDetailFromArguments(args);
            }
        }

        // Configurar botón de reservar
        reservarButton.setOnClickListener(v -> {
            if (!classId.isEmpty()) {
                // Reservar clase real usando el backend
                reservarButton.setEnabled(false); // Deshabilitar botón durante la reserva
                reservarButton.setText("Reservando...");
                
                bookingRepository.createBooking(classId, new RepositoryCallback<BookingResponse>() {
                    @Override
                    public void onSuccess(BookingResponse bookingResponse) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "¡Clase reservada exitosamente!", Toast.LENGTH_SHORT).show();
                                // Navegar de vuelta al home
                                Navigation.findNavController(view).popBackStack();
                            });
                        }
                    }

                    @Override
                    public void onError(String error) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Error al reservar: " + error, Toast.LENGTH_LONG).show();
                                // Restaurar botón
                                reservarButton.setEnabled(true);
                                reservarButton.setText("RESERVAR");
                            });
                        }
                    }
                });
            } else {
                // Fallback para flujo legacy (sin classId)
                Toast.makeText(getContext(), "Error: No se puede reservar clase sin ID válido", Toast.LENGTH_LONG).show();
            }
        });

        // Configurar botón de volver
        backButton.setOnClickListener(v -> {
            Navigation.findNavController(view).popBackStack();
        });

        // Configurar botón de ver mapa
        verMapaButton.setOnClickListener(v -> {
            openMap();
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

    private void loadClassDetailFromBackend(String classId) {
        // Mostrar loading (opcional)
        // TODO: Agregar indicador de carga
        
        scheduleRepository.getClassDetail(classId, new RepositoryCallback<ScheduledClassDto>() {
            @Override
            public void onSuccess(ScheduledClassDto scheduledClass) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        populateViewsWithBackendData(scheduledClass);
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error al cargar detalle: " + error, Toast.LENGTH_LONG).show();
                        // Opcional: navegar de vuelta
                        Navigation.findNavController(requireView()).popBackStack();
                    });
                }
            }
        });
    }

    private void loadClassDetailFromArguments(Bundle args) {
        disciplina = args.getString("disciplina", "");
        hora = args.getString("hora", "");
        profesor = args.getString("profesor", "");
        cupos = args.getString("cupos", "");
        duracion = args.getString("duracion", "");
        sede = args.getString("sede", "");
        ubicacion = args.getString("ubicacion", "");
        fecha = args.getString("fecha", "");

        populateViewsWithLocalData();
    }

    private void populateViewsWithBackendData(ScheduledClassDto scheduledClass) {
        classTitle.setText(scheduledClass.getName());
        classProfesor.setText("Profesor: " + scheduledClass.getProfessor());
        
        // Formatear fecha y hora desde el backend
        // Nota: Aquí deberías parsear scheduledClass.getDateTime() apropiadamente
        // Por simplicidad, usamos el string directamente
        classHorario.setText("Horario: " + formatDateTime(scheduledClass.getDateTime()));
        classFecha.setText("Fecha: " + formatDate(scheduledClass.getDateTime()));
        
        classCupos.setText("Cupos disponibles: " + scheduledClass.getAvailableSlots());
        classDuracion.setText("Duración: " + scheduledClass.getDurationMinutes() + " min");
        
        // Por ahora, ubicación no está en el backend, usar valor por defecto
        classUbicacion.setText("Ubicación: Por definir");
        
        // Descripción basada en el nombre de la clase
        String descripcion = getDescripcionPorDisciplina(scheduledClass.getName());
        classDescripcion.setText(descripcion);
    }

    private void populateViewsWithLocalData() {
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
    }

    private String formatDateTime(String dateTimeString) {
        // Implementación simple - en producción deberías parsear correctamente
        // la fecha del formato ISO del backend
        return dateTimeString != null ? dateTimeString.substring(11, 16) : "N/A";
    }

    private String formatDate(String dateTimeString) {
        // Implementación simple - en producción deberías parsear correctamente
        return dateTimeString != null ? dateTimeString.substring(0, 10) : "N/A";
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

    private void openMap() {
        double latitude = -34.61709214740957;
        double longitude = -58.38197224422459;

        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Uri webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude);
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
            startActivity(webIntent);
        }
    }
}
