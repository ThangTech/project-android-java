package com.example.project_android_java.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DB_NAME = "millionaire.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_QUESTIONS = "questions";
    public static final String TABLE_LEADERBOARD = "leaderboard";

    private static final String COL_ID = "id";
    private static final String COL_QUESTION = "question";
    private static final String COL_OPTION_A = "option_a";
    private static final String COL_OPTION_B = "option_b";
    private static final String COL_OPTION_C = "option_c";
    private static final String COL_OPTION_D = "option_d";
    private static final String COL_CORRECT = "correct";
    private static final String COL_LEVEL = "level";
    private static final String COL_CATEGORY = "category";

    private static final String COL_PLAYER_NAME = "player_name";
    private static final String COL_SCORE = "score";
    private static final String COL_QUESTIONS_CORRECT = "questions_correct";
    private static final String COL_DATE_PLAYED = "date_played";

    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEADERBOARD);
        createTables(db);
    }

    private void createTables(SQLiteDatabase db) {
        String createQuestionsTable = "CREATE TABLE " + TABLE_QUESTIONS + " (" +
                COL_ID + " INTEGER PRIMARY KEY, " +
                COL_QUESTION + " TEXT NOT NULL, " +
                COL_OPTION_A + " TEXT NOT NULL, " +
                COL_OPTION_B + " TEXT NOT NULL, " +
                COL_OPTION_C + " TEXT NOT NULL, " +
                COL_OPTION_D + " TEXT NOT NULL, " +
                COL_CORRECT + " INTEGER NOT NULL, " +
                COL_LEVEL + " INTEGER NOT NULL, " +
                COL_CATEGORY + " TEXT" +
                ")";

        String createLeaderboardTable = "CREATE TABLE " + TABLE_LEADERBOARD + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PLAYER_NAME + " TEXT NOT NULL, " +
                COL_SCORE + " INTEGER NOT NULL, " +
                COL_QUESTIONS_CORRECT + " INTEGER NOT NULL, " +
                COL_DATE_PLAYED + " TEXT NOT NULL" +
                ")";

        db.execSQL(createQuestionsTable);
        db.execSQL(createLeaderboardTable);

        Log.d(TAG, "Tables created successfully");
    }

    public void importQuestionsFromJson() {
        importQuestionsFromJson(false);
    }

    public void importQuestionsFromJson(boolean forceReload) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (forceReload) {
            db.delete(TABLE_QUESTIONS, null, null);
            Log.d(TAG, "Deleted old questions for reload");
        }
        
        if (forceReload || isQuestionsEmpty()) {
            try {
                String jsonString = readJsonFromAssets("question.json");
                JSONObject root = new JSONObject(jsonString);
                JSONArray questions = root.getJSONArray("questions");
                List<ContentValues> questionList = new ArrayList<>();

                for (int i = 0; i < questions.length(); i++) {
                    JSONObject obj = questions.getJSONObject(i);
                    ContentValues values = new ContentValues();
                    values.put(COL_ID, obj.getInt("id"));
                    values.put(COL_QUESTION, obj.getString("question"));
                    values.put(COL_OPTION_A, obj.getJSONArray("options").getString(0));
                    values.put(COL_OPTION_B, obj.getJSONArray("options").getString(1));
                    values.put(COL_OPTION_C, obj.getJSONArray("options").getString(2));
                    values.put(COL_OPTION_D, obj.getJSONArray("options").getString(3));
                    values.put(COL_CORRECT, obj.getInt("correct"));
                    values.put(COL_LEVEL, obj.getInt("level"));
                    values.put(COL_CATEGORY, obj.optString("category", "General"));
                    questionList.add(values);
                }

                for (ContentValues cv : questionList) {
                    db.insert(TABLE_QUESTIONS, null, cv);
                }

                Log.d(TAG, "Imported " + questionList.size() + " questions from JSON");
            } catch (Exception e) {
                Log.e(TAG, "Error importing questions: " + e.getMessage());
            }
        }
    }

    private String readJsonFromAssets(String fileName) throws Exception {
        InputStream is = context.getAssets().open(fileName);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        return new String(buffer, StandardCharsets.UTF_8);
    }

    public boolean isQuestionsEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_QUESTIONS, null);
        boolean empty = true;
        if (cursor.moveToFirst()) {
            empty = cursor.getInt(0) == 0;
        }
        cursor.close();
        return empty;
    }

    public List<String[]> getQuestionsByLevel(int level) {
        List<String[]> questions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_QUESTIONS, null, COL_LEVEL + " = ?",
                new String[]{String.valueOf(level)}, null, null, "RANDOM()");

        while (cursor.moveToNext()) {
            String[] q = {
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_QUESTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_OPTION_A)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_OPTION_B)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_OPTION_C)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_OPTION_D)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CORRECT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY))
            };
            questions.add(q);
        }
        cursor.close();
        return questions;
    }

    public int getQuestionCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_QUESTIONS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public long insertScore(String playerName, long score, int questionsCorrect) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PLAYER_NAME, playerName);
        values.put(COL_SCORE, score);
        values.put(COL_QUESTIONS_CORRECT, questionsCorrect);
        values.put(COL_DATE_PLAYED, String.valueOf(System.currentTimeMillis()));
        return db.insert(TABLE_LEADERBOARD, null, values);
    }

    public List<String[]> getTopScores(int limit) {
        List<String[]> scores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_LEADERBOARD +
                " ORDER BY " + COL_SCORE + " DESC LIMIT ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(limit)});

        while (cursor.moveToNext()) {
            String[] s = {
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PLAYER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_SCORE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_QUESTIONS_CORRECT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE_PLAYED))
            };
            scores.add(s);
        }
        cursor.close();
        return scores;
    }

    public long getHighScore() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(" + COL_SCORE + ") FROM " + TABLE_LEADERBOARD, null);
        long highScore = 0;
        if (cursor.moveToFirst()) {
            highScore = cursor.getLong(0);
        }
        cursor.close();
        return highScore;
    }

    public List<String[]> getAllQuestions() {
        List<String[]> questions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_QUESTIONS, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String[] q = {
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_QUESTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_OPTION_A)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_OPTION_B)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_OPTION_C)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_OPTION_D)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CORRECT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_LEVEL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY))
            };
            questions.add(q);
        }
        cursor.close();
        return questions;
    }

    public void close() {
        super.close();
    }
}