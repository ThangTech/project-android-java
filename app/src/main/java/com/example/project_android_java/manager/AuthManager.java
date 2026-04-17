package com.example.project_android_java.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.project_android_java.database.DatabaseHelper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AuthManager {

    private static final String TAG = "AuthManager";
    private static final String PREFS_NAME = "UserSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";

    private static AuthManager instance;
    private final SharedPreferences prefs;
    private final DatabaseHelper dbHelper;

    private AuthManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        dbHelper = new DatabaseHelper(context);
    }

    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context.getApplicationContext());
        }
        return instance;
    }

    public boolean register(String username, String password) {
        if (username == null || username.trim().length() < 3 || username.trim().length() > 20) {
            return false;
        }
        if (password == null || password.length() < 4) {
            return false;
        }

        String usernameTrimmed = username.trim();

        // Check if username already exists
        if (dbHelper.getUserIdByUsername(usernameTrimmed) > 0) {
            Log.d(TAG, "Registration failed: username already exists");
            return false;
        }

        // Generate salt and hash password
        String salt = generateSalt();
        String hash = hashPassword(password, salt);

        if (hash == null) {
            return false;
        }

        int userId = dbHelper.insertUser(usernameTrimmed, hash, salt);
        if (userId > 0) {
            saveSession(userId, usernameTrimmed);
            return true;
        }
        return false;
    }

    public int login(String username, String password) {
        if (username == null || password == null) {
            return -1;
        }

        String usernameTrimmed = username.trim();
        String[] credentials = dbHelper.getUserCredentials(usernameTrimmed);

        if (credentials == null) {
            Log.d(TAG, "Login failed: user not found");
            return -1;
        }

        String storedHash = credentials[1];
        String storedSalt = credentials[2];
        int userId = Integer.parseInt(credentials[0]);

        String hash = hashPassword(password, storedSalt);
        if (hash != null && hash.equals(storedHash)) {
            saveSession(userId, usernameTrimmed);
            Log.d(TAG, "Login success: " + usernameTrimmed);
            return userId;
        }

        Log.d(TAG, "Login failed: wrong password");
        return -1;
    }

    public void logout() {
        prefs.edit().remove(KEY_USER_ID).remove(KEY_USERNAME).apply();
        Log.d(TAG, "User logged out");
    }

    public boolean isLoggedIn() {
        return prefs.getInt(KEY_USER_ID, -1) > 0;
    }

    public int getCurrentUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public String getCurrentUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    private void saveSession(int userId, String username) {
        prefs.edit()
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_USERNAME, username)
                .apply();
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
            Log.e(TAG, "SHA-256 not available", e);
            return null;
        }
    }
}
