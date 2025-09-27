package com.ritmofit.app.ui.reservations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ritmofit.app.R;
import com.ritmofit.app.data.RitmoFitApiService;
import com.ritmofit.app.data.api.BookingService;
import com.ritmofit.app.data.api.model.UserBookingDto;
import com.ritmofit.app.data.repository.BookingRepository;
import com.ritmofit.app.data.repository.RepositoryCallback;
import java.util.List;

public class ReservationsFragment extends Fragment {
    
    private BookingRepository bookingRepository;
    private LinearLayout myReservationsContainer;
    private TextView noReservationsText;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservations, container, false);

        // Inicializar repositorio
        BookingService bookingService = RitmoFitApiService.getClient(getContext()).create(BookingService.class);
        bookingRepository = new BookingRepository(bookingService);

        myReservationsContainer = view.findViewById(R.id.myReservationsContainer);
        noReservationsText = view.findViewById(R.id.noReservationsText);

        // Cargar reservas del backend
        loadMyReservations();


        return view;
    }

    private void loadMyReservations() {
        bookingRepository.getMyBookings(new RepositoryCallback<List<UserBookingDto>>() {
            @Override
            public void onSuccess(List<UserBookingDto> reservations) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        displayReservations(reservations);
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Log detallado del error
                        android.util.Log.e("ReservationsFragment", "Error detallado: " + error);
                        
                        Toast.makeText(getContext(), "Error de conexión: " + error, Toast.LENGTH_LONG).show();
                        // Mostrar mensaje de no reservas si hay error
                        myReservationsContainer.removeAllViews();
                        noReservationsText.setVisibility(View.VISIBLE);
                    });
                }
            }
        });
    }

    private void displayReservations(List<UserBookingDto> reservations) {
        myReservationsContainer.removeAllViews();

        if (reservations.isEmpty()) {
            noReservationsText.setVisibility(View.VISIBLE);
            return;
        }

        noReservationsText.setVisibility(View.GONE);

        for (UserBookingDto reservation : reservations) {
            createReservationCard(reservation);
        }
    }

    private void createReservationCard(UserBookingDto reservation) {
        // Crear layout para la reserva con fondo blanco y borde naranja
        LinearLayout card = new LinearLayout(getContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        card.setBackgroundResource(R.drawable.reservation_card);
        card.setPadding(24, 18, 24, 18);
        card.setElevation(4f);

        // Contenedor horizontal para el contenido principal
        LinearLayout contentContainer = new LinearLayout(getContext());
        contentContainer.setOrientation(LinearLayout.HORIZONTAL);
        contentContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        // Contenedor vertical para la información de la clase
        LinearLayout infoContainer = new LinearLayout(getContext());
        infoContainer.setOrientation(LinearLayout.VERTICAL);
        infoContainer.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));

        // Título de la clase
        TextView classNameView = new TextView(getContext());
        classNameView.setText(reservation.getClassName());
        classNameView.setTextSize(18);
        classNameView.setTextColor(getResources().getColor(R.color.ritmofit_orange, null));
        classNameView.setTypeface(null, android.graphics.Typeface.BOLD);

        // Profesor
        TextView professorView = new TextView(getContext());
        professorView.setText("Profesor: " + reservation.getProfessor());
        professorView.setTextSize(14);

        // Fecha y hora
        TextView dateTimeView = new TextView(getContext());
        dateTimeView.setText(reservation.getFormattedDate() + " a las " + reservation.getFormattedTime());
        dateTimeView.setTextSize(14);

        // Estado
        TextView statusView = new TextView(getContext());
        statusView.setText("Estado: " + reservation.getStatus());
        statusView.setTextSize(12);
        statusView.setTextColor(getResources().getColor(android.R.color.darker_gray, null));

        infoContainer.addView(classNameView);
        infoContainer.addView(professorView);
        infoContainer.addView(dateTimeView);
        infoContainer.addView(statusView);

        // Botón cancelar con estilo naranja
        Button deleteButton = new Button(getContext());
        deleteButton.setText("Cancelar");
        deleteButton.setTextSize(14);
        deleteButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        
        // Aplicar estilo naranja de RitmoFit (mismo estilo que otros botones)
        deleteButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
            getResources().getColor(R.color.ritmofit_orange, null)));
        deleteButton.setTextColor(getResources().getColor(android.R.color.white, null));
        deleteButton.setPadding(24, 12, 24, 12);
        deleteButton.setAllCaps(false); // Para mantener el texto "Cancelar" en lugar de "CANCELAR"
        deleteButton.setOnClickListener(btn -> {
            new AlertDialog.Builder(getContext())
                .setTitle("Cancelar reserva")
                .setMessage("¿Estás seguro que deseas cancelar la reserva para " + reservation.getClassName() + "?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    // Cancelar reserva en el backend
                    bookingRepository.cancelBooking(reservation.getBookingId(), new RepositoryCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), "Reserva cancelada exitosamente", Toast.LENGTH_SHORT).show();
                                    // Remover visualmente la tarjeta
                                    myReservationsContainer.removeView(card);
                                    if (myReservationsContainer.getChildCount() == 0) {
                                        noReservationsText.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onError(String error) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), "Error al cancelar: " + error, Toast.LENGTH_LONG).show();
                                });
                            }
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
        });

        contentContainer.addView(infoContainer);
        contentContainer.addView(deleteButton);
        card.addView(contentContainer);

        // Agregar margen inferior
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) card.getLayoutParams();
        params.setMargins(0, 0, 0, 24);
        card.setLayoutParams(params);

        myReservationsContainer.addView(card);
    }
}