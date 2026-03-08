package com.example.project_android_java.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.project_android_java.R;

/**
 * MAIN ACTIVITY - Màn hình chính của app.
 *
 * 📚 Kiến thức cần biết:
 *  - Activity lifecycle: onCreate() là điểm khởi đầu
 *  - setContentView(): gắn XML layout vào Activity
 *  - findViewById(): tìm View theo ID trong XML
 *  - Intent: dùng để chuyển sang Activity khác
 *  - setOnClickListener(): xử lý sự kiện bấm nút
 */
public class MainActivity extends AppCompatActivity {


    private Button btnPlay;
    private Button btnInstructions;
    private Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bước 1: Gắn file XML layout với Activity này
        setContentView(R.layout.activity_main);

        // Bước 2: Tìm các View từ XML theo ID
        initViews();

        // Bước 3: Gắn sự kiện click cho từng nút
        setupClickListeners();
    }

    /**
     * Khởi tạo các View bằng cách tìm theo ID trong XML.
     * ID phải khớp với android:id trong file activity_main.xml
     */
    private void initViews() {
        btnPlay         = findViewById(R.id.btn_play);
        btnInstructions = findViewById(R.id.btn_instructions);
        btnExit         = findViewById(R.id.btn_exit);
    }

    /**
     * Gắn sự kiện click cho từng nút.
     */
    private void setupClickListeners() {

        // Nút "Bắt đầu chơi" → chuyển sang GameActivity
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGame();
            }
        });

        // Nút "Hướng dẫn" → hiện dialog hướng dẫn
        btnInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInstructionsDialog();
            }
        });

        // Nút "Thoát" → hỏi xác nhận rồi thoát app
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitConfirmDialog();
            }
        });
    }

    // ── Điều hướng ───────────────────────────────────────────────────────────

    /**
     * Chuyển sang màn hình chơi game.
     *
     * Intent = "ý định chuyển đến Activity khác"
     * - tham số 1: context hiện tại (this)
     * - tham số 2: class Activity muốn chuyển đến
     */
    private void goToGame() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    // ── Dialog ───────────────────────────────────────────────────────────────

    /**
     * Hiện hộp thoại hướng dẫn chơi.
     * AlertDialog.Builder = cách tạo popup trong Android.
     */
    private void showInstructionsDialog() {
        String instructions =
                "🎯 LUẬT CHƠI\n\n" +
                        "• 15 câu hỏi, độ khó tăng dần\n" +
                        "• Mỗi câu có 4 đáp án A, B, C, D\n" +
                        "• Trả lời đúng → tiếp tục\n" +
                        "• Trả lời sai → kết thúc\n\n" +
                        "🆘 TRỢ GIÚP (mỗi loại dùng 1 lần)\n\n" +
                        "• 50:50 — Loại 2 đáp án sai\n" +
                        "• 📞 Gọi điện — Nhận gợi ý từ chuyên gia\n" +
                        "• 👥 Hỏi khán giả — Xem % bình chọn\n\n" +
                        "💰 MỐC AN TOÀN\n\n" +
                        "• Câu 5: 2.000.000đ\n" +
                        "• Câu 10: 22.000.000đ";

        new AlertDialog.Builder(this)
                .setTitle("📖 Hướng dẫn chơi")
                .setMessage(instructions)
                .setPositiveButton("Đã hiểu!", null)
                .show();
    }

    /**
     * Hiện hộp thoại xác nhận thoát app.
     */
    private void showExitConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Thoát game")
                .setMessage("Bạn có chắc muốn thoát không?")
                .setPositiveButton("Thoát", (dialog, which) -> {
                    // finish() = đóng Activity hiện tại
                    // Vì MainActivity là root, app sẽ thoát
                    finish();
                })
                .setNegativeButton("Ở lại", null)
                .show();
    }
}