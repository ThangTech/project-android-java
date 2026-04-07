package com.example.project_android_java.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_android_java.R;

public class ResultActivity extends AppCompatActivity {

    private TextView tvResultTitle, tvMoneyEarned;
    private EditText edtPlayerName;
    private Button btnSaveScore, btnPlayAgain, btnMainMenu;
    private long finalScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

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
        finalScore    = getIntent().getLongExtra("MONEY_EARNED", 0);

        if (isWin) {
            tvResultTitle.setText("🎉 CHÚC MỪNG!");
            tvResultTitle.setTextColor(0xFFFFD700);
        } else {
            tvResultTitle.setText("💔 RẤT TIẾC!");
            tvResultTitle.setTextColor(0xFFFF6B6B);
        }

        tvMoneyEarned.setText(GameActivity.formatMoney(finalScore));
    }

    private void savePlayerScore() {
        String name = edtPlayerName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên của bạn!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Logic lưu điểm (Sau này sẽ kết nối Database/Leaderboard)
        // Hiện tại chúng ta thông báo thành công
        Toast.makeText(this, "Đã lưu điểm của " + name + ": " + GameActivity.formatMoney(finalScore), Toast.LENGTH_LONG).show();
        
        // Vô hiệu hóa nút sau khi lưu để tránh lưu trùng
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