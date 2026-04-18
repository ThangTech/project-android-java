package com.example.project_android_java.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_android_java.R;
import com.example.project_android_java.database.DatabaseHelper;
import com.example.project_android_java.manager.QuestionManager;

public class AdminActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        dbHelper = new DatabaseHelper(this);

        initViews();
    }

    private void initViews() {
        Button btnManageQuestions = findViewById(R.id.btn_manage_questions);
        Button btnStatistics = findViewById(R.id.btn_statistics);
        Button btnReloadQuestions = findViewById(R.id.btn_reload_questions);
        Button btnLogout = findViewById(R.id.btn_logout);

        btnManageQuestions.setOnClickListener(v -> {
            startActivity(new Intent(this, QuestionManagementActivity.class));
        });

        btnStatistics.setOnClickListener(v -> {
            startActivity(new Intent(this, StatisticsActivity.class));
        });

        btnReloadQuestions.setOnClickListener(v -> showReloadConfirmDialog());

        btnLogout.setOnClickListener(v -> showLogoutConfirmDialog());
    }

    private void showReloadConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Nạp lại câu hỏi")
                .setMessage("Điều này sẽ xóa tất cả câu hỏi hiện tại và nạp lại từ file JSON. Tiếp tục?")
                .setPositiveButton("Nạp lại", (dialog, which) -> {
                    dbHelper.importQuestionsFromJson(true);
                    QuestionManager.getInstance(this).loadQuestions();
                    Toast.makeText(this, "Đã nạp lại câu hỏi!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showLogoutConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất Admin")
                .setMessage("Bạn có chắc muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}