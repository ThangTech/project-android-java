package com.example.project_android_java.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_android_java.R;
import com.example.project_android_java.manager.AuthManager;
import com.example.project_android_java.manager.ScoreManager;

import java.util.ArrayList;
import java.util.List;

public class RankActivity extends AppCompatActivity {

    private ListView lvRank;
    private TextView tvHighScore;
    private Button btnBack;
    private ScoreManager scoreManager;
    private AuthManager authManager;
    private List<String[]> allScores;
    private List<String[]> myScores;
    private int currentFilter = 0; // 0 = all, 1 = mine

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        scoreManager = ScoreManager.getInstance(this);
        authManager = AuthManager.getInstance(this);
        initViews();
        loadLeaderboard();
    }

    private void initViews() {
        lvRank = findViewById(R.id.lv_rank);
        tvHighScore = findViewById(R.id.tv_high_score);
        btnBack = findViewById(R.id.btn_back);
        Button btnAll = findViewById(R.id.btn_all);
        Button btnMine = findViewById(R.id.btn_mine);

        btnBack.setOnClickListener(v -> finish());

        btnAll.setOnClickListener(v -> {
            currentFilter = 0;
            updateFilterUI(btnAll, btnMine);
            loadLeaderboard();
        });

        btnMine.setOnClickListener(v -> {
            if (!authManager.isLoggedIn()) {
                Toast.makeText(this, "Vui lòng đăng nhập để xem điểm của bạn", Toast.LENGTH_SHORT).show();
                return;
            }
            currentFilter = 1;
            updateFilterUI(btnAll, btnMine);
            loadLeaderboard();
        });
    }

    private void updateFilterUI(Button selected, Button unselected) {
        selected.setBackgroundResource(R.drawable.btn_rect_purple);
        unselected.setBackgroundResource(R.drawable.btn_round_orange);
    }

    private void loadLeaderboard() {
        if (currentFilter == 1 && authManager.isLoggedIn()) {
            int userId = authManager.getCurrentUserId();
            myScores = scoreManager.getTopScoresByUser(20, userId);
            long highScore = scoreManager.getHighScoreByUser(userId);
            tvHighScore.setText("Điểm cao nhất của bạn: " + GameActivity.formatMoney(highScore));

            if (myScores.isEmpty()) {
                Toast.makeText(this, "Bạn chưa có điểm nào!", Toast.LENGTH_SHORT).show();
            }
            RankAdapter adapter = new RankAdapter(this, myScores);
            lvRank.setAdapter(adapter);
        } else {
            allScores = scoreManager.getTopScores(20);
            long highScore = scoreManager.getHighScore();
            tvHighScore.setText("Điểm cao nhất: " + GameActivity.formatMoney(highScore));

            if (allScores.isEmpty()) {
                Toast.makeText(this, "Chưa có điểm!", Toast.LENGTH_SHORT).show();
            }
            RankAdapter adapter = new RankAdapter(this, allScores);
            lvRank.setAdapter(adapter);
        }
    }

    static class RankAdapter extends ArrayAdapter<String[]> {
        private final List<String[]> scores;

        public RankAdapter(Context context, List<String[]> scores) {
            super(context, R.layout.item_rank);
            this.scores = scores;
        }

        @Override
        public int getCount() {
            return scores.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_rank, parent, false);
            }

            String[] score = scores.get(position);
            String rank = String.valueOf(position + 1);
            String name = score[0];
            String money = GameActivity.formatMoney(Long.parseLong(score[1]));

            TextView tvRankNumber = convertView.findViewById(R.id.tv_rank_number);
            TextView tvPlayerName = convertView.findViewById(R.id.tv_player_name);
            TextView tvScore = convertView.findViewById(R.id.tv_score);

            tvRankNumber.setText(rank);
            tvPlayerName.setText(name);
            tvScore.setText(money);

            return convertView;
        }

        @Override
        public String[] getItem(int position) {
            return scores.get(position);
        }
    }
}