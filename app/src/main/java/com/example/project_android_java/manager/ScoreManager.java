package com.example.project_android_java.manager;

import android.content.Context;

import com.example.project_android_java.database.DatabaseHelper;

import java.util.List;

public class ScoreManager {

    private static ScoreManager instance;
    private Context context;
    private DatabaseHelper dbHelper;

    private ScoreManager(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = new DatabaseHelper(context);
    }

    public static ScoreManager getInstance(Context context) {
        if (instance == null) {
            instance = new ScoreManager(context);
        }
        return instance;
    }

    public long saveScore(String playerName, long score, int questionsCorrect) {
        return dbHelper.insertScore(playerName, score, questionsCorrect);
    }

    public List<String[]> getTopScores(int limit) {
        return dbHelper.getTopScores(limit);
    }

    public long getHighScore() {
        return dbHelper.getHighScore();
    }

    public boolean isNewHighScore(long score) {
        return score > getHighScore();
    }

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}