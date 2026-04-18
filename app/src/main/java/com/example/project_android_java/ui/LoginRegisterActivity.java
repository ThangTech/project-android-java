package com.example.project_android_java.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_android_java.R;
import com.example.project_android_java.database.DatabaseHelper;
import com.example.project_android_java.manager.AuthManager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class LoginRegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private TextView tvTitle, tvError;
    private Button btnSubmit, btnToggleMode, btnGuest, btnForgotPassword;
    private AuthManager authManager;
    private DatabaseHelper dbHelper;

    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authManager = AuthManager.getInstance(this);
        dbHelper = new DatabaseHelper(this);

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
        btnForgotPassword = findViewById(R.id.btn_forgot_password);
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
        btnForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
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

    private void showForgotPasswordDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_forgot_password);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.btn_rect_purple);

        EditText etUsername = dialog.findViewById(R.id.et_username);
        EditText etPassword = dialog.findViewById(R.id.et_password);
        EditText etConfirmPassword = dialog.findViewById(R.id.et_confirm_password);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        btnSave.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            if (username.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên đăng nhập", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 4) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 4 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            int userId = dbHelper.getUserIdByUsername(username);
            if (userId <= 0) {
                Toast.makeText(this, "Tên đăng nhập không tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }

            String salt = generateSalt();
            String hash = hashPassword(password, salt);
            dbHelper.updateUserPassword(userId, hash, salt);
            Toast.makeText(this, "Đặt lại mật khẩu thành công!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String saltedPassword = password + salt;
            byte[] hash = md.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
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
