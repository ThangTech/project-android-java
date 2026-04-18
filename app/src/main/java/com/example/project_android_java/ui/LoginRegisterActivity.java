package com.example.project_android_java.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_android_java.R;
import com.example.project_android_java.manager.AuthManager;

public class LoginRegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private TextView tvTitle, tvError;
    private Button btnSubmit, btnToggleMode, btnGuest;
    private AuthManager authManager;

    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authManager = AuthManager.getInstance(this);

        // Already logged in -> go to MainActivity directly
        if (authManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        setContentView(R.layout.activity_login_register);

        initViews();
        setupListeners();
    }

    @Override
    public void onBackPressed() {
        // Guest pressing back -> go to MainActivity
        navigateToMain();
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        tvTitle = findViewById(R.id.tv_title);
        tvError = findViewById(R.id.tv_error);
        btnSubmit = findViewById(R.id.btn_submit);
        btnToggleMode = findViewById(R.id.btn_toggle_mode);
        btnGuest = findViewById(R.id.btn_guest);
    }

    private void setupListeners() {
        btnSubmit.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            tvError.setVisibility(View.GONE);

            if (username.trim().isEmpty() || password.isEmpty()) {
                showError("Vui lòng nhập đầy đủ thông tin");
                return;
            }

            if (isLoginMode) {
                // Check admin login
                if (username.equals("admin") && password.equals("admin123")) {
                    Toast.makeText(this, "Đăng nhập Admin thành công!", Toast.LENGTH_SHORT).show();
                    navigateToAdmin();
                    return;
                }

                int userId = authManager.login(username, password);
                if (userId > 0) {
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                } else {
                    showError("Tên đăng nhập hoặc mật khẩu không đúng");
                }
            } else {
                if (username.trim().length() < 3) {
                    showError("Tên đăng nhập phải có ít nhất 3 ký tự");
                    return;
                }
                if (username.trim().length() > 20) {
                    showError("Tên đăng nhập tối đa 20 ký tự");
                    return;
                }
                if (password.length() < 4) {
                    showError("Mật khẩu phải có ít nhất 4 ký tự");
                    return;
                }

                boolean success = authManager.register(username, password);
                if (success) {
                    Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                } else {
                    showError("Tên đăng nhập đã tồn tại");
                }
            }
        });

        btnToggleMode.setOnClickListener(v -> toggleMode());
        btnGuest.setOnClickListener(v -> navigateToMain());
    }

    private void toggleMode() {
        isLoginMode = !isLoginMode;
        if (isLoginMode) {
            tvTitle.setText("ĐĂNG NHẬP");
            btnSubmit.setText("ĐĂNG NHẬP");
            btnToggleMode.setText("Chưa có tài khoản? Đăng ký");
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            tvTitle.setText("ĐĂNG KÝ");
            btnSubmit.setText("ĐĂNG KÝ");
            btnToggleMode.setText("Đã có tài khoản? Đăng nhập");
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        tvError.setVisibility(View.GONE);
        etUsername.setText("");
        etPassword.setText("");
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToAdmin() {
        Intent intent = new Intent(this, AdminActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
