package com.example.project_android_java.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.project_android_java.R;
import com.example.project_android_java.database.DatabaseHelper;

import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        dbHelper = new DatabaseHelper(this);
        loadStatistics();
    }

    private void loadStatistics() {
        int totalQuestions = dbHelper.getQuestionCount();
        long highScore = dbHelper.getHighScore();
        int totalPlayers = dbHelper.getUniquePlayersCount();

        int[] questionsByLevel = new int[15];
        List<String[]> allQuestions = dbHelper.getAllQuestions();
        for (String[] q : allQuestions) {
            int level = Integer.parseInt(q[7]);
            if (level >= 1 && level <= 15) {
                questionsByLevel[level - 1]++;
            }
        }

        int easyCount = 0, mediumCount = 0, hardCount = 0;
        for (int i = 0; i < 5; i++) easyCount += questionsByLevel[i];
        for (int i = 5; i < 10; i++) mediumCount += questionsByLevel[i];
        for (int i = 10; i < 15; i++) hardCount += questionsByLevel[i];

        TextView tvTotalQuestions = findViewById(R.id.tv_total_questions);
        TextView tvHighScore = findViewById(R.id.tv_high_score);
        TextView tvTotalPlayers = findViewById(R.id.tv_total_players);
        TextView tvEasyCount = findViewById(R.id.tv_easy_count);
        TextView tvMediumCount = findViewById(R.id.tv_medium_count);
        TextView tvHardCount = findViewById(R.id.tv_hard_count);
        LinearLayout llLevelContainer = findViewById(R.id.ll_level_container);

        tvTotalQuestions.setText(String.valueOf(totalQuestions));
        tvHighScore.setText(String.valueOf(highScore));
        tvTotalPlayers.setText(String.valueOf(totalPlayers));
        tvEasyCount.setText(easyCount + " câu");
        tvMediumCount.setText(mediumCount + " câu");
        tvHardCount.setText(hardCount + " câu");

        int maxInLevel = 1;
        for (int count : questionsByLevel) {
            if (count > maxInLevel) maxInLevel = count;
        }

        llLevelContainer.removeAllViews();
        for (int i = 0; i < 15; i++) {
            LinearLayout levelRow = new LinearLayout(this);
            levelRow.setOrientation(LinearLayout.HORIZONTAL);
            levelRow.setGravity(Gravity.CENTER_VERTICAL);
            levelRow.setPadding(0, 6, 0, 6);

            TextView tvLevel = new TextView(this);
            tvLevel.setText("Lv" + (i + 1));
            tvLevel.setTextColor(getLevelColor(i + 1));
            tvLevel.setTextSize(14);
            tvLevel.setWidth(70);

            int barMaxWidth = 600;
            int barWidth = 0;
            if (maxInLevel > 0) {
                barWidth = (int) ((questionsByLevel[i] / (float) maxInLevel) * barMaxWidth);
            }
            final int color = getLevelColor(i + 1);
            View bar = new View(this);
            bar.setBackgroundColor(color);
            LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(barWidth, 16);
            barParams.setMargins(8, 0, 8, 0);
            bar.setLayoutParams(barParams);

            TextView tvCount = new TextView(this);
            tvCount.setText(" " + questionsByLevel[i] + " ");
            tvCount.setTextColor(getLevelColor(i + 1));
            tvCount.setTextSize(14);

            levelRow.addView(tvLevel);
            levelRow.addView(bar);
            levelRow.addView(tvCount);
            llLevelContainer.addView(levelRow);
        }
    }

    private int getLevelColor(int level) {
        if (level <= 5) return 0xFF4CAF50;
        if (level <= 10) return 0xFFFF9800;
        return 0xFFF44336;
    }
}
