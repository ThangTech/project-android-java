package com.example.project_android_java.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_android_java.R;
import com.example.project_android_java.database.DatabaseHelper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

public class AccountManagementActivity extends AppCompatActivity {

    private ListView lvAccounts;
    private TextView tvEmpty, tvAccountCount;
    private Button btnAddAccount, btnBack;
    private DatabaseHelper dbHelper;
    private List<String[]> accounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        dbHelper = new DatabaseHelper(this);
        initViews();
        loadAccounts();
    }

    private void initViews() {
        lvAccounts = findViewById(R.id.lv_accounts);
        tvEmpty = findViewById(R.id.tv_empty);
        tvAccountCount = findViewById(R.id.tv_account_count);
        btnAddAccount = findViewById(R.id.btn_add_account);
        btnBack = findViewById(R.id.btn_back);

        btnAddAccount.setOnClickListener(v -> showEditDialog(-1, null));
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadAccounts() {
        accounts = dbHelper.getAllUsers();
        Log.d("AccountDebug", "loadAccounts: found " + accounts.size() + " accounts");
        for (int i = 0; i < accounts.size(); i++) {
            String[] u = accounts.get(i);
            Log.d("AccountDebug", "Account[" + i + "]: id=" + u[0] + ", username=" + u[1] + ", hash=" + u[2] + ", salt=" + u[3] + ", created=" + u[4]);
        }

        if (accounts.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            lvAccounts.setVisibility(View.GONE);
            tvAccountCount.setText("Tổng tài khoản: 0");
        } else {
            tvEmpty.setVisibility(View.GONE);
            lvAccounts.setVisibility(View.VISIBLE);
            tvAccountCount.setText("Tổng tài khoản: " + accounts.size());

            AccountAdapter adapter = new AccountAdapter(this, accounts);
            lvAccounts.setAdapter(adapter);
        }
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

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private void showEditDialog(int userId, String[] userData) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_account);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.btn_rect_purple);

        EditText etUsername = dialog.findViewById(R.id.et_username);
        EditText etPassword = dialog.findViewById(R.id.et_password);
        EditText etConfirmPassword = dialog.findViewById(R.id.et_confirm_password);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        if (userData != null) {
            etUsername.setText(userData[1]);
        }

        btnSave.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            if (username.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên đăng nhập", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userId == -1) {
                if (password.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                int existingUserId = dbHelper.getUserIdByUsername(username);
                if (existingUserId > 0) {
                    Toast.makeText(this, "Tên đăng nhập đã tồn tại!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String salt = generateSalt();
                String hash = hashPassword(password, salt);
                Log.d("AccountDebug", "Inserting user: username=" + username + ", salt=" + salt + ", hash=" + hash);
                long result = dbHelper.insertUser(username, hash, salt);
                Log.d("AccountDebug", "Insert result: " + result);
                if (result <= 0) {
                    Toast.makeText(this, "Không thể tạo tài khoản. Thử lại!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, "Đã thêm tài khoản mới!", Toast.LENGTH_SHORT).show();
            } else {
                if (!password.isEmpty()) {
                    if (!password.equals(confirmPassword)) {
                        Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String salt = generateSalt();
                    String hash = hashPassword(password, salt);
                    Log.d("AccountDebug", "Updating user " + userId + ": username=" + username + ", salt=" + salt + ", hash=" + hash);
                    dbHelper.updateUser(userId, username, hash, salt);
                    Toast.makeText(this, "Đã cập nhật tài khoản!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("AccountDebug", "Updating username for user " + userId + ": " + username);
                    dbHelper.updateUsername(userId, username);
                    Toast.makeText(this, "Đã cập nhật tên đăng nhập!", Toast.LENGTH_SHORT).show();
                }
            }

            dialog.dismiss();
            loadAccounts();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showDeleteConfirmDialog(int userId, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa tài khoản")
                .setMessage("Bạn có chắc muốn xóa tài khoản này? Toàn bộ lịch sử chơi game của tài khoản cũng sẽ bị xóa.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    dbHelper.deleteUser(userId);
                    Toast.makeText(this, "Đã xóa tài khoản!", Toast.LENGTH_SHORT).show();
                    loadAccounts();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    class AccountAdapter extends ArrayAdapter<String[]> {
        private final List<String[]> data;

        public AccountAdapter(Context context, List<String[]> data) {
            super(context, R.layout.item_account);
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_account, parent, false);
            }

            String[] user = data.get(position);
            int userId = Integer.parseInt(user[0]);

            TextView tvUsername = convertView.findViewById(R.id.tv_username);
            TextView tvAccountInfo = convertView.findViewById(R.id.tv_account_info);
            Button btnEdit = convertView.findViewById(R.id.btn_edit);
            Button btnDelete = convertView.findViewById(R.id.btn_delete);

            tvUsername.setText(user[1]);

            long createdAt = 0;
            try {
                createdAt = Long.parseLong(user[4]);
            } catch (NumberFormatException e) {
                createdAt = 0;
            }
            String dateStr = "Ngày tạo: " + (createdAt > 0
                    ? new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                        .format(new java.util.Date(createdAt))
                    : "N/A");
            tvAccountInfo.setText(dateStr);

            btnEdit.setOnClickListener(v -> showEditDialog(userId, user));
            btnDelete.setOnClickListener(v -> showDeleteConfirmDialog(userId, position));

            return convertView;
        }

        @Override
        public String[] getItem(int position) {
            return data.get(position);
        }
    }
}