package com.example.project_android_java.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.project_android_java.R;
import com.example.project_android_java.manager.AuthManager;

public class MainActivity extends AppCompatActivity {

    private Button btnPlay;
    private ImageButton btnInfo, btnRank, btnSettings;
    private MediaPlayer introPlayer;
    private SharedPreferences prefs;
    private AuthManager authManager;
    private TextView tvWelcome;
    private Button btnLogin, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("GameSettings", Context.MODE_PRIVATE);
        authManager = AuthManager.getInstance(this);

        initViews();
        updateUserUI();
        setupClickListeners();
        playIntroMusic();
    }

    private void initViews() {
        btnPlay = findViewById(R.id.btn_play);
        btnInfo = findViewById(R.id.btn_info);
        btnRank = findViewById(R.id.btn_rank);
        btnSettings = findViewById(R.id.btn_settings);
        tvWelcome = findViewById(R.id.tv_welcome);
        btnLogin = findViewById(R.id.btn_login);
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void updateUserUI() {
        if (authManager.isLoggedIn()) {
            String username = authManager.getCurrentUsername();
            tvWelcome.setText("Xin chào, " + username + "!");
            tvWelcome.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            tvWelcome.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        btnPlay.setOnClickListener(v -> {
            stopIntroMusic();
            startActivity(new Intent(MainActivity.this, GameActivity.class));
        });

        btnInfo.setOnClickListener(v -> showInfoDialog());
        btnRank.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RankActivity.class)));
        btnSettings.setOnClickListener(v -> showSettingsDialog());

        btnLogin.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginRegisterActivity.class)));

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc muốn đăng xuất?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {
                        authManager.logout();
                        updateUserUI();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    private void playIntroMusic() {
        boolean musicEnabled = prefs.getBoolean("music_enabled", true);
        if (musicEnabled) {
            if (introPlayer == null) {
                introPlayer = MediaPlayer.create(this, R.raw.intro_music);
                if (introPlayer != null) {
                    introPlayer.setLooping(true);
                    introPlayer.start();
                }
            } else if (!introPlayer.isPlaying()) {
                introPlayer.start();
            }
        }
    }

    private void stopIntroMusic() {
        if (introPlayer != null) {
            if (introPlayer.isPlaying()) {
                introPlayer.stop();
            }
            introPlayer.release();
            introPlayer = null;
        }
    }

    private void showInfoDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Thông tin trò chơi")
                .setMessage("Trò chơi Ai Là Triệu Phú - Phiên bản Android Java.\nPhát triển bởi Nguyễn Văn Thắng")
                .setPositiveButton("Đóng", null)
                .show();
    }

    private void showSettingsDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null);
        SwitchCompat swMusic = dialogView.findViewById(R.id.sw_music);
        SwitchCompat swSound = dialogView.findViewById(R.id.sw_sound);

        swMusic.setChecked(prefs.getBoolean("music_enabled", true));
        swSound.setChecked(prefs.getBoolean("sound_enabled", true));

        new AlertDialog.Builder(this)
                .setTitle("Cài đặt")
                .setView(dialogView)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("music_enabled", swMusic.isChecked());
                    editor.putBoolean("sound_enabled", swSound.isChecked());
                    editor.apply();

                    if (swMusic.isChecked()) {
                        playIntroMusic();
                    } else {
                        stopIntroMusic();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playIntroMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopIntroMusic();
    }
}