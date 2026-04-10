package com.example.project_android_java.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_android_java.R;
import com.example.project_android_java.manager.ScoreManager;

public class ResultActivity extends AppCompatActivity {

    private TextView tvResultTitle, tvMoneyEarned;
    private EditText edtPlayerName;
    private Button btnSaveScore, btnPlayAgain, btnMainMenu;
    private long finalScore = 0;
    private int questionsCorrect = 0;
    private ScoreManager scoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        scoreManager = ScoreManager.getInstance(this);
        initViews();
        displayResult();
    }

    private void initViews() {
        tvResultTitle = findViewById(R.id.tv_result_title);
        tvMoneyEarned = findViewById(R.id.tv_money_earned);
        edtPlayerName = findViewById(R.id.edt_player_name);
        btnSaveScore  = findViewById(R.id.btn_save_score);
        btnPlayAgain  = findViewById(R.id.btn_play_again);
        btnMainMenu   = findViewById(R.id.btn_main_menu);

        btnSaveScore.setOnClickListener(v -> savePlayerScore());
        btnPlayAgain.setOnClickListener(v -> playAgain());
        btnMainMenu.setOnClickListener(v -> goToMainMenu());
    }

    private void displayResult() {
        boolean isWin = getIntent().getBooleanExtra("IS_WIN", false);
        finalScore = getIntent().getLongExtra("MONEY_EARNED", 0);
        questionsCorrect = getIntent().getIntExtra("QUESTIONS_CORRECT", 0);

        if (isWin) {
            tvResultTitle.setText("CHUC MONG!");
            tvResultTitle.setTextColor(0xFFFFD700);
        } else {
            tvResultTitle.setText("RAT TIEC!");
            tvResultTitle.setTextColor(0xFFFF6B6B);
        }

        tvMoneyEarned.setText(GameActivity.formatMoney(finalScore));
    }

    private void savePlayerScore() {
        String name = edtPlayerName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Vui long nhap ten cua ban!", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = scoreManager.saveScore(name, finalScore, questionsCorrect);
        if (result > 0) {
            Toast.makeText(this, "Da luu diem cua " + name + ": " + GameActivity.formatMoney(finalScore), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Loi luu diem!", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSaveScore.setEnabled(false);
        btnSaveScore.setAlpha(0.5f);
        edtPlayerName.setEnabled(false);
    }

    private void playAgain() {
        startActivity(new Intent(this, GameActivity.class));
        finish();
    }

    private void goToMainMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}