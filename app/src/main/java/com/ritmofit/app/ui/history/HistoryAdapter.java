package com.ritmofit.app.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ritmofit.app.R;
import com.ritmofit.app.data.api.model.HistoryItemResponse;
import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.VH> {

    private final List<HistoryItemResponse> data = new ArrayList<>();

    public void submit(List<HistoryItemResponse> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        HistoryItemResponse it = data.get(pos);
        h.tvTitle.setText(it.discipline);
        // Ej: "15/09 · 18:00 — Sede Centro"
        String whenWhere = prettyWhen(it.startDateTime) + " — " + it.site;
        h.tvWhenWhere.setText(whenWhere);
        // Ej: "Prof: Laura Pérez · Duración: 60'"
        String extra = "Prof: " + safe(it.teacher) + " · Duración: " + it.durationMinutes + "'";
        h.tvExtra.setText(extra);
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvWhenWhere, tvExtra;
        VH(@NonNull View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvWhenWhere = v.findViewById(R.id.tvWhenWhere);
            tvExtra = v.findViewById(R.id.tvExtra);
        }
    }

    private static String safe(String s){ return s==null? "—" : s; }

    // Recorta ISO "YYYY-MM-DDTHH:MM" -> "dd/MM · HH:mm"
    private static String prettyWhen(String iso) {
        if (iso == null || iso.length()<16) return "—";
        // asume formato ISO básico
        String d = iso.substring(8,10) + "/" + iso.substring(5,7);
        String t = iso.substring(11,16);
        return d + " · " + t;
    }
}