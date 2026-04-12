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
import com.example.project_android_java.manager.ScoreManager;

import java.util.ArrayList;
import java.util.List;

public class RankActivity extends AppCompatActivity {

    private ListView lvRank;
    private TextView tvHighScore;
    private Button btnBack;
    private ScoreManager scoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        scoreManager = ScoreManager.getInstance(this);
        initViews();
        loadLeaderboard();
    }

    private void initViews() {
        lvRank = findViewById(R.id.lv_rank);
        tvHighScore = findViewById(R.id.tv_high_score);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadLeaderboard() {
        long highScore = scoreManager.getHighScore();
        tvHighScore.setText("Điểm cao nhất: " + GameActivity.formatMoney(highScore));

        List<String[]> topScores = scoreManager.getTopScores(20);

        if (topScores.isEmpty()) {
            Toast.makeText(this, "Chưa có điểm!", Toast.LENGTH_SHORT).show();
            return;
        }

        RankAdapter adapter = new RankAdapter(this, topScores);
        lvRank.setAdapter(adapter);
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