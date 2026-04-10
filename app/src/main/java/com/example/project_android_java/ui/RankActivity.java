package com.example.project_android_java.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
        tvHighScore.setText("Diem cao nhat: " + GameActivity.formatMoney(highScore));

        List<String[]> topScores = scoreManager.getTopScores(20);

        if (topScores.isEmpty()) {
            Toast.makeText(this, "Chua co diem!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> displayList = new ArrayList<>();
        for (int i = 0; i < topScores.size(); i++) {
            String[] score = topScores.get(i);
            String display = (i + 1) + ". " + score[0] + " - " + GameActivity.formatMoney(Long.parseLong(score[1]));
            displayList.add(display);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_rank, displayList);
        lvRank.setAdapter(adapter);
    }
}