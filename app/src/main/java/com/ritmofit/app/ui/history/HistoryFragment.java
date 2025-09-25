package com.ritmofit.app.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ritmofit.app.R;
import com.ritmofit.app.data.api.model.HistoryItemResponse;
import com.ritmofit.app.data.repository.HistoryRepository;
import com.ritmofit.app.data.repository.RepositoryCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private RecyclerView rv;
    private HistoryAdapter adapter;
    private HistoryRepository repo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv = view.findViewById(R.id.rvHistory);
        adapter = new HistoryAdapter();
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        adapter.setOnItemClick(item -> {
            android.os.Bundle args = new android.os.Bundle();
            args.putString("attendanceId", item.id);
            androidx.navigation.fragment.NavHostFragment.findNavController(this)
                    .navigate(R.id.action_historyFragment_to_historyDetailFragment, args);
        });

        repo = new HistoryRepository(requireContext());

        String[] range = last30Days(); // [from, to] formato YYYY-MM-DD
        load(range[0], range[1]);
    }

    private void load(String from, String to) {
        repo.getMyHistory(from, to, new RepositoryCallback<List<HistoryItemResponse>>() {
            @Override public void onSuccess(List<HistoryItemResponse> data) {
                adapter.submit(data);
            }
            @Override public void onError(String msg) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Rango últimos 30 días (inclusive), formato YYYY-MM-DD
    private String[] last30Days() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar to = Calendar.getInstance();
        Calendar from = (Calendar) to.clone();
        from.add(Calendar.DAY_OF_YEAR, -30);
        return new String[]{ f.format(from.getTime()), f.format(to.getTime()) };
    }
}