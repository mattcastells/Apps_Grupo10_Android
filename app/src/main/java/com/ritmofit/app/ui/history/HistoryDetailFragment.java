package com.ritmofit.app.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ritmofit.app.R;
import com.ritmofit.app.data.api.model.HistoryDetailResponse;
import com.ritmofit.app.data.repository.HistoryRepository;
import com.ritmofit.app.data.repository.RepositoryCallback;

import java.util.Locale;

public class HistoryDetailFragment extends Fragment {

    private HistoryRepository repo;

    private TextView tvSite, tvDate, tvTime, tvDuration, tvDiscipline, tvTeacher, tvStatus;
    private TextView tvNoReview, tvReviewTitle, tvReviewComment;
    private ImageView s1, s2, s3, s4, s5;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        repo = new HistoryRepository(requireContext());

        tvSite = v.findViewById(R.id.tvSite);
        tvDate = v.findViewById(R.id.tvDate);
        tvTime = v.findViewById(R.id.tvTime);
        tvDuration = v.findViewById(R.id.tvDuration);
        tvDiscipline = v.findViewById(R.id.tvDiscipline);
        tvTeacher = v.findViewById(R.id.tvTeacher);
        tvStatus = v.findViewById(R.id.tvStatus);

        tvNoReview = v.findViewById(R.id.tvNoReview);
        tvReviewTitle = v.findViewById(R.id.tvReviewTitle);
        tvReviewComment = v.findViewById(R.id.tvReviewComment);

        s1 = v.findViewById(R.id.star1);
        s2 = v.findViewById(R.id.star2);
        s3 = v.findViewById(R.id.star3);
        s4 = v.findViewById(R.id.star4);
        s5 = v.findViewById(R.id.star5);

        String attendanceId = getArguments()!=null ? getArguments().getString("attendanceId") : null;
        if (attendanceId == null || attendanceId.isEmpty()) {
            Toast.makeText(requireContext(), "Falta attendanceId", Toast.LENGTH_SHORT).show();
            return;
        }

        load(attendanceId);
    }

    private void load(String attendanceId) {
        repo.getDetail(attendanceId, new RepositoryCallback<HistoryDetailResponse>() {
            @Override public void onSuccess(HistoryDetailResponse d) { bind(d); }
            @Override public void onError(String msg) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bind(HistoryDetailResponse d) {
        // Sede
        tvSite.setText(s("Sede: %s", n(d.site)));

        // Fecha y hora a partir de ISO "YYYY-MM-DDTHH:MM:SS"
        String iso = d.startDateTime != null ? d.startDateTime : "";
        String fecha = iso.length()>=10 ? iso.substring(8,10)+"/"+iso.substring(5,7)+"/"+iso.substring(0,4) : "—";
        String hora  = iso.length()>=16 ? iso.substring(11,16) : "—";
        tvDate.setText(fecha);
        tvTime.setText("Horario: " + hora);

        // Duración
        tvDuration.setText("Duración: " + humanMinutes(d.durationMinutes));

        // Disciplina / Instructor
        tvDiscipline.setText("Disciplina: " + n(d.discipline));
        tvTeacher.setText("Instructor: " + n(d.teacher));

        // Estado
        tvStatus.setText("Estado de la asistencia: " + n(d.attendanceStatus));

        // Reseña del usuario
        if (d.userReview == null || d.userReview.rating == null) {
            tvNoReview.setVisibility(View.VISIBLE);
            tvReviewTitle.setVisibility(View.GONE);
            vStars(false,0);
            tvReviewComment.setVisibility(View.GONE);
        } else {
            tvNoReview.setVisibility(View.GONE);
            tvReviewTitle.setVisibility(View.VISIBLE);
            vStars(true, clamp(d.userReview.rating));
            if (d.userReview.comment != null && !d.userReview.comment.isEmpty()) {
                tvReviewComment.setText(d.userReview.comment);
                tvReviewComment.setVisibility(View.VISIBLE);
            } else {
                tvReviewComment.setVisibility(View.GONE);
            }
        }
    }

    private void vStars(boolean show, int rating){
        int vis = show ? View.VISIBLE : View.GONE;
        s1.setVisibility(vis); s2.setVisibility(vis); s3.setVisibility(vis); s4.setVisibility(vis); s5.setVisibility(vis);
        if (!show) return;
        setStar(s1, rating>=1); setStar(s2, rating>=2); setStar(s3, rating>=3); setStar(s4, rating>=4); setStar(s5, rating>=5);
    }
    private void setStar(ImageView iv, boolean filled){
        iv.setImageResource(filled ? R.drawable.ic_star_24 : R.drawable.ic_star_border_24);
    }
    private static int clamp(Integer r){ return r==null?0:Math.max(0, Math.min(5, r)); }
    private static String humanMinutes(int m){
        if (m % 60 == 0) return (m/60) + " hora" + ((m/60)==1? "":"s");
        return m + " minutos";
    }
    private static String n(String s){ return (s==null||s.isEmpty())? "—" : s; }
    private static String s(String f, Object... a){ return String.format(Locale.getDefault(), f, a); }
}