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
    
    // Elementos de la UI
    private TextView className, classTime, classDate, classProfessor, classDuration, 
                     classCapacity, classLocation, classDescription;
    private Button bookClassButton, backButton;
    
    // Datos de la clase (se pasan desde HomeFragment)
    private String classId, classNameStr, classTimeStr, classDateStr, classProfessorStr, 
                   classDurationStr, classCapacityStr, classLocationStr, classDescriptionStr;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class_detail, container, false);
        Bundle args = getArguments();
        if (args != null) {
            classId = args.getString("classId");
            classNameStr = args.getString("className");
            classTimeStr = args.getString("classTime");
            classDateStr = args.getString("classDate");
            classProfessorStr = args.getString("classProfessor");
            classDurationStr = args.getString("classDuration");
            classCapacityStr = args.getString("classCapacity");
            classLocationStr = args.getString("classLocation");
            classDescriptionStr = args.getString("classDescription");
        }
        
        initializeViews(view);
        setupClassData();
        setupButtons(view);
        return view;
    }
    
    private void initializeViews(View view) {
        className = view.findViewById(R.id.className);
        classTime = view.findViewById(R.id.classTime);
        classDate = view.findViewById(R.id.classDate);
        classProfessor = view.findViewById(R.id.classProfessor);
        classDuration = view.findViewById(R.id.classDuration);
        classCapacity = view.findViewById(R.id.classCapacity);
        classLocation = view.findViewById(R.id.classLocation);
        classDescription = view.findViewById(R.id.classDescription);
        bookClassButton = view.findViewById(R.id.bookClassButton);
        backButton = view.findViewById(R.id.backButton);
    }
    
    private void setupClassData() {
        if (classNameStr != null) {
            className.setText(classNameStr);
            classTime.setText("Horario: " + classTimeStr);
            classDate.setText("Fecha: " + classDateStr);
            classProfessor.setText("Profesor: " + classProfessorStr);
            classDuration.setText("Duración: " + classDurationStr);
            classCapacity.setText("Cupos: " + classCapacityStr);
            classLocation.setText("Ubicación: " + classLocationStr);
            classDescription.setText(classDescriptionStr != null ? classDescriptionStr : "Descripción no disponible");
        }
    }
    
    private void setupButtons(View view) {
        bookClassButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Funcionalidad de reserva en desarrollo", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigate(R.id.reservationsFragment);
        });
        backButton.setOnClickListener(v -> {
            Navigation.findNavController(view).popBackStack();
        });
    }
}
