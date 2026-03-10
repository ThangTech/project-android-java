package com.example.project_android_java.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_android_java.R;

/**
 * RESULT ACTIVITY - Màn hình kết quả sau khi game kết thúc.
 *
 * 📚 Kiến thức cần biết:
 *  - getIntent().getExtra(): nhận dữ liệu từ Activity trước
 *  - intent.addFlags(): điều khiển back stack
 */
public class ResultActivity extends AppCompatActivity {

    private TextView tvResultTitle;
    private TextView tvMoneyEarned;
    private TextView tvQuestionReached;
    private Button btnPlayAgain;
    private Button btnMainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        initViews();
        displayResult();
    }

    private void initViews() {
        tvResultTitle      = findViewById(R.id.tv_result_title);
        tvMoneyEarned      = findViewById(R.id.tv_money_earned);
        tvQuestionReached  = findViewById(R.id.tv_question_reached);
        btnPlayAgain       = findViewById(R.id.btn_play_again);
        btnMainMenu        = findViewById(R.id.btn_main_menu);

        btnPlayAgain.setOnClickListener(v -> playAgain());
        btnMainMenu.setOnClickListener(v -> goToMainMenu());
    }

    /**
     * Nhận dữ liệu từ GameActivity và hiển thị lên màn hình.
     * getIntent(): lấy Intent đã dùng để mở Activity này
     * getBooleanExtra(key, defaultValue): lấy giá trị boolean
     * getLongExtra(key, defaultValue): lấy giá trị long
     */
    private void displayResult() {
        boolean isWin         = getIntent().getBooleanExtra("IS_WIN", false);
        long moneyEarned      = getIntent().getLongExtra("MONEY_EARNED", 0);
        int questionReached   = getIntent().getIntExtra("QUESTION_REACHED", 1);

        if (isWin) {
            tvResultTitle.setText("🎉 CHÚC MỪNG!");
            tvResultTitle.setTextColor(0xFFFFD700); // Vàng
        } else {
            tvResultTitle.setText("💔 RẤT TIẾC!");
            tvResultTitle.setTextColor(0xFFFF6B6B); // Đỏ
        }

        tvMoneyEarned.setText(GameActivity.formatMoney(moneyEarned));
        tvQuestionReached.setText("Đã trả lời đúng " + (questionReached - (isWin ? 0 : 1)) + " / 15 câu");
    }

    private void playAgain() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToMainMenu() {
        // FLAG_ACTIVITY_CLEAR_TOP: xóa tất cả Activity ở trên MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
