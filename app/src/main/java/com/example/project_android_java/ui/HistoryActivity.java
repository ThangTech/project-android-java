package com.example.project_android_java.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_android_java.R;
import com.example.project_android_java.manager.AuthManager;
import com.example.project_android_java.manager.ScoreManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private ListView lvHistory;
    private TextView tvEmpty;
    private TextView tvHistoryCount;
    private Button btnBack;
    private Button btnClearHistory;
    private ScoreManager scoreManager;
    private AuthManager authManager;
    private List<String[]> gameHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scoreManager = ScoreManager.getInstance(this);
        authManager = AuthManager.getInstance(this);

        if (!authManager.isLoggedIn()) {
            setContentView(R.layout.activity_history_guest);
            initViewsGuest();
            return;
        }

        setContentView(R.layout.activity_history);
        initViews();
        loadHistory();
    }

    private void initViews() {
        lvHistory = findViewById(R.id.lv_history);
        tvEmpty = findViewById(R.id.tv_empty);
        tvHistoryCount = findViewById(R.id.tv_history_count);
        btnBack = findViewById(R.id.btn_back);
        btnClearHistory = findViewById(R.id.btn_clear_history);

        btnBack.setOnClickListener(v -> finish());
        btnClearHistory.setOnClickListener(v -> showClearConfirmDialog());
    }

    private void initViewsGuest() {
        btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void loadHistory() {
        int userId = authManager.getCurrentUserId();
        gameHistory = scoreManager.getGameHistoryByUser(userId);

        if (gameHistory == null) {
            gameHistory = new ArrayList<>();
        }

        if (gameHistory.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            lvHistory.setVisibility(View.GONE);
            tvHistoryCount.setText("Tổng số trận: 0");
        } else {
            tvEmpty.setVisibility(View.GONE);
            lvHistory.setVisibility(View.VISIBLE);
            tvHistoryCount.setText("Tổng số trận: " + gameHistory.size());

            HistoryAdapter adapter = new HistoryAdapter(this, gameHistory);
            lvHistory.setAdapter(adapter);
        }
    }

    private void showClearConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa lịch sử")
                .setMessage("Bạn có chắc muốn xóa toàn bộ lịch sử chơi? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int userId = authManager.getCurrentUserId();
                    scoreManager.deleteGameHistoryByUser(userId);
                    loadHistory();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    static class HistoryAdapter extends ArrayAdapter<String[]> {
        private final List<String[]> history;

        public HistoryAdapter(Context context, List<String[]> history) {
            super(context, R.layout.item_history);
            this.history = history;
        }

        @Override
        public int getCount() {
            return history.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_history, parent, false);
            }

            String[] game = history.get(position);

            TextView tvDate = convertView.findViewById(R.id.tv_date);
            TextView tvScore = convertView.findViewById(R.id.tv_score);
            TextView tvQuestionsCorrect = convertView.findViewById(R.id.tv_questions_correct);
            TextView tvLevelReached = convertView.findViewById(R.id.tv_level_reached);

            String dateStr = formatDateStatic(game[3]);
            tvDate.setText(dateStr);

            long score = Long.parseLong(game[1]);
            tvScore.setText(GameActivity.formatMoney(score));

            int qCorrect = Integer.parseInt(game[2]);
            tvQuestionsCorrect.setText(qCorrect + "/15 câu đúng");
            tvLevelReached.setText("Mức: " + qCorrect);

            return convertView;
        }

        @Override
        public String[] getItem(int position) {
            return history.get(position);
        }

        private static String formatDateStatic(String epochMillis) {
            try {
                long timestamp = Long.parseLong(epochMillis);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("vi-VN"));
                return sdf.format(new Date(timestamp));
            } catch (Exception e) {
                return epochMillis;
            }
        }
    }
}
