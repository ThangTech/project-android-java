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
        return dbHelper.insertScore(-1, playerName, score, questionsCorrect);
    }

    public long saveScore(int userId, String playerName, long score, int questionsCorrect) {
        return dbHelper.insertScore(userId, playerName, score, questionsCorrect);
    }

    public List<String[]> getTopScores(int limit) {
        return dbHelper.getTopScores(limit);
    }

    public List<String[]> getTopScoresByUser(int limit, int userId) {
        return dbHelper.getTopScores(limit, userId);
    }

    public long getHighScore() {
        return dbHelper.getHighScore();
    }

    public long getHighScoreByUser(int userId) {
        return dbHelper.getHighScoreByUser(userId);
    }

    public List<String[]> getGameHistoryByUser(int userId) {
        return dbHelper.getGameHistoryByUser(userId);
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