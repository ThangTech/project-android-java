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
    private static final int DB_VERSION = 3;

    public static final String TABLE_QUESTIONS = "questions";
    public static final String TABLE_LEADERBOARD = "leaderboard";
    public static final String TABLE_USERS = "users";

    public static final String COL_USER_ID = "user_id";

    private static final String COL_ID = "id";
    private static final String COL_QUESTION = "question";
    private static final String COL_OPTION_A = "option_a";
    private static final String COL_OPTION_B = "option_b";
    private static final String COL_OPTION_C = "option_c";
    private static final String COL_OPTION_D = "option_d";
    private static final String COL_CORRECT = "correct";
    private static final String COL_LEVEL = "level";
    private static final String COL_CATEGORY = "category";
    private static final String COL_EVIDENCE = "evidence";

    private static final String COL_PLAYER_NAME = "player_name";
    private static final String COL_SCORE = "score";
    private static final String COL_QUESTIONS_CORRECT = "questions_correct";
    private static final String COL_DATE_PLAYED = "date_played";

    // Users table columns
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD_HASH = "password_hash";
    private static final String COL_PASSWORD_SALT = "password_salt";
    private static final String COL_CREATED_AT = "created_at";

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
        if (oldVersion < 2) {
            migrateToV2(db);
        }
        if (oldVersion < 3) {
            migrateToV3(db);
        }
    }

    private void migrateToV2(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + TABLE_QUESTIONS + " ADD COLUMN " + COL_EVIDENCE + " TEXT");
    }

    private void migrateToV3(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT NOT NULL UNIQUE, " +
                COL_PASSWORD_HASH + " TEXT NOT NULL, " +
                COL_PASSWORD_SALT + " TEXT NOT NULL, " +
                COL_CREATED_AT + " TEXT NOT NULL" +
                ")";
        db.execSQL(createUsersTable);
        db.execSQL("ALTER TABLE " + TABLE_LEADERBOARD + " ADD COLUMN " + COL_USER_ID + " INTEGER REFERENCES " + TABLE_USERS + "(" + COL_ID + ")");
        Log.d(TAG, "Migrated to v3: users table created, user_id added to leaderboard");
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
                COL_CATEGORY + " TEXT, " +
                COL_EVIDENCE + " TEXT" +
                ")";

        String createLeaderboardTable = "CREATE TABLE " + TABLE_LEADERBOARD + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID + " INTEGER REFERENCES " + TABLE_USERS + "(" + COL_ID + "), " +
                COL_PLAYER_NAME + " TEXT NOT NULL, " +
                COL_SCORE + " INTEGER NOT NULL, " +
                COL_QUESTIONS_CORRECT + " INTEGER NOT NULL, " +
                COL_DATE_PLAYED + " TEXT NOT NULL" +
                ")";

        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT NOT NULL UNIQUE, " +
                COL_PASSWORD_HASH + " TEXT NOT NULL, " +
                COL_PASSWORD_SALT + " TEXT NOT NULL, " +
                COL_CREATED_AT + " TEXT NOT NULL" +
                ")";

        db.execSQL(createQuestionsTable);
        db.execSQL(createLeaderboardTable);
        db.execSQL(createUsersTable);

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
                    values.put(COL_EVIDENCE, obj.optString("evidence", ""));
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
        return insertScore(-1, playerName, score, questionsCorrect);
    }

    public long insertScore(int userId, String playerName, long score, int questionsCorrect) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (userId > 0) {
            values.put(COL_USER_ID, userId);
        }
        values.put(COL_PLAYER_NAME, playerName);
        values.put(COL_SCORE, score);
        values.put(COL_QUESTIONS_CORRECT, questionsCorrect);
        values.put(COL_DATE_PLAYED, String.valueOf(System.currentTimeMillis()));
        long result = db.insert(TABLE_LEADERBOARD, null, values);
        Log.d(TAG, "insertScore: userId=" + userId + ", name=" + playerName + ", score=" + score + ", result=" + result);
        return result;
    }

    public int insertUser(String username, String passwordHash, String passwordSalt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD_HASH, passwordHash);
        values.put(COL_PASSWORD_SALT, passwordSalt);
        values.put(COL_CREATED_AT, String.valueOf(System.currentTimeMillis()));
        return (int) db.insert(TABLE_USERS, null, values);
    }

    public int getUserIdByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_ID}, COL_USERNAME + " = ?",
                new String[]{username}, null, null, null);
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
        }
        cursor.close();
        return userId;
    }

    public List<String[]> getAllUsers() {
        List<String[]> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, null, null, null, null, COL_ID + " ASC");

        while (cursor.moveToNext()) {
            String[] u = {
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD_HASH)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD_SALT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CREATED_AT))
            };
            users.add(u);
        }
        cursor.close();
        return users;
    }

    public int updateUser(int userId, String username, String passwordHash, String passwordSalt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD_HASH, passwordHash);
        values.put(COL_PASSWORD_SALT, passwordSalt);
        int rows = db.update(TABLE_USERS, values, COL_ID + " = ?", new String[]{String.valueOf(userId)});
        Log.d(TAG, "updateUser id=" + userId + ": rows=" + rows);
        return rows;
    }

    public int updateUsername(int userId, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        int rows = db.update(TABLE_USERS, values, COL_ID + " = ?", new String[]{String.valueOf(userId)});
        Log.d(TAG, "updateUsername id=" + userId + ": rows=" + rows);
        return rows;
    }

    public int updateUserPassword(int userId, String passwordHash, String passwordSalt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PASSWORD_HASH, passwordHash);
        values.put(COL_PASSWORD_SALT, passwordSalt);
        int rows = db.update(TABLE_USERS, values, COL_ID + " = ?", new String[]{String.valueOf(userId)});
        Log.d(TAG, "updateUserPassword id=" + userId + ": rows=" + rows);
        return rows;
    }

    public int deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LEADERBOARD, COL_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        int rows = db.delete(TABLE_USERS, COL_ID + " = ?", new String[]{String.valueOf(userId)});
        Log.d(TAG, "deleteUser id=" + userId + ": rows=" + rows);
        return rows;
    }

    public String[] getUserCredentials(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_ID, COL_PASSWORD_HASH, COL_PASSWORD_SALT},
                COL_USERNAME + " = ?",
                new String[]{username}, null, null, null);
        String[] result = null;
        if (cursor.moveToFirst()) {
            result = new String[]{
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD_HASH)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD_SALT))
            };
        }
        cursor.close();
        return result;
    }

    public String getUsernameById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_USERNAME}, COL_ID + " = ?",
                new String[]{String.valueOf(userId)}, null, null, null);
        String username = null;
        if (cursor.moveToFirst()) {
            username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME));
        }
        cursor.close();
        return username;
    }

    public List<String[]> getTopScores(int limit) {
        return getTopScores(limit, -1);
    }

    public List<String[]> getTopScores(int limit, int filterUserId) {
        List<String[]> scores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query;
        if (filterUserId > 0) {
            query = "SELECT * FROM " + TABLE_LEADERBOARD +
                    " WHERE " + COL_USER_ID + " = ?" +
                    " ORDER BY " + COL_SCORE + " DESC LIMIT ?";
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(filterUserId), String.valueOf(limit)});
            while (cursor.moveToNext()) {
                String[] s = {
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PLAYER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SCORE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_QUESTIONS_CORRECT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE_PLAYED)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ID))
                };
                scores.add(s);
            }
            cursor.close();
        } else {
            query = "SELECT * FROM " + TABLE_LEADERBOARD +
                    " ORDER BY " + COL_SCORE + " DESC LIMIT ?";
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(limit)});
            while (cursor.moveToNext()) {
                String[] s = {
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PLAYER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SCORE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_QUESTIONS_CORRECT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE_PLAYED)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ID))
                };
                scores.add(s);
            }
            cursor.close();
        }
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

    public long getHighScoreByUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(" + COL_SCORE + ") FROM " + TABLE_LEADERBOARD + " WHERE " + COL_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        long highScore = 0;
        if (cursor.moveToFirst()) {
            highScore = cursor.getLong(0);
        }
        cursor.close();
        return highScore;
    }

    public int getUniquePlayersCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(DISTINCT " + COL_USER_ID + ") FROM " + TABLE_LEADERBOARD + " WHERE " + COL_USER_ID + " IS NOT NULL", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public List<String[]> getGameHistoryByUser(int userId) {
        List<String[]> history = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_LEADERBOARD +
                " WHERE " + COL_USER_ID + " = ?" +
                " ORDER BY " + COL_DATE_PLAYED + " DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        while (cursor.moveToNext()) {
            String[] h = {
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_SCORE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_QUESTIONS_CORRECT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE_PLAYED)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PLAYER_NAME))
            };
            history.add(h);
        }
        cursor.close();
        return history;
    }

    public void deleteGameHistoryByUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LEADERBOARD, COL_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        Log.d(TAG, "Deleted game history for userId: " + userId);
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
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_EVIDENCE))
            };
            questions.add(q);
        }
        cursor.close();
        return questions;
    }

    public long insertQuestion(String question, String optionA, String optionB, String optionC, String optionD, int correct, int level, String category, String evidence) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_QUESTION, question);
        values.put(COL_OPTION_A, optionA);
        values.put(COL_OPTION_B, optionB);
        values.put(COL_OPTION_C, optionC);
        values.put(COL_OPTION_D, optionD);
        values.put(COL_CORRECT, correct);
        values.put(COL_LEVEL, level);
        values.put(COL_CATEGORY, category);
        values.put(COL_EVIDENCE, evidence);
        long result = db.insert(TABLE_QUESTIONS, null, values);
        Log.d(TAG, "insertQuestion: result=" + result);
        return result;
    }

    public int updateQuestion(int id, String question, String optionA, String optionB, String optionC, String optionD, int correct, int level, String category, String evidence) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_QUESTION, question);
        values.put(COL_OPTION_A, optionA);
        values.put(COL_OPTION_B, optionB);
        values.put(COL_OPTION_C, optionC);
        values.put(COL_OPTION_D, optionD);
        values.put(COL_CORRECT, correct);
        values.put(COL_LEVEL, level);
        values.put(COL_CATEGORY, category);
        values.put(COL_EVIDENCE, evidence);
        int rows = db.update(TABLE_QUESTIONS, values, COL_ID + " = ?", new String[]{String.valueOf(id)});
        Log.d(TAG, "updateQuestion id=" + id + ": rows=" + rows);
        return rows;
    }

    public int deleteQuestion(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_QUESTIONS, COL_ID + " = ?", new String[]{String.valueOf(id)});
        Log.d(TAG, "deleteQuestion id=" + id + ": rows=" + rows);
        return rows;
    }

    public void close() {
        super.close();
    }
}