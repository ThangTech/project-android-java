package com.example.project_android_java.manager;

import android.content.Context;
import android.util.Log;

import com.example.project_android_java.model.Question;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * QUESTION MANAGER - Quản lý toàn bộ ngân hàng câu hỏi.
 *
 * 📚 Kiến thức cần biết:
 *  - Singleton Pattern: chỉ tạo 1 instance duy nhất trong cả app
 *  - InputStream: đọc file từ assets/
 *  - JSONObject / JSONArray: parse dữ liệu JSON
 *  - Collections.shuffle(): trộn ngẫu nhiên danh sách
 *  - try-catch: xử lý lỗi khi đọc file
 */
public class QuestionManager {

    private static final String TAG = "QuestionManager";

    // ── Singleton ────────────────────────────────────────────────────────────
    // Chỉ có 1 instance duy nhất của class này trong toàn app
    private static QuestionManager instance;

    private Context context;
    private List<Question> allQuestions; // Toàn bộ câu hỏi từ JSON

    // Constructor private → bên ngoài không tạo trực tiếp được
    private QuestionManager(Context context) {
        this.context = context.getApplicationContext();
        this.allQuestions = new ArrayList<>();
    }


    public static QuestionManager getInstance(Context context) {
        if (instance == null) {
            instance = new QuestionManager(context);
        }
        return instance;
    }

    // ── Load câu hỏi từ JSON ─────────────────────────────────────────────────

    public boolean loadQuestions() {
        try {
            // Bước 1: Đọc file JSON từ assets thành String
            String jsonString = readJsonFromAssets("question.json");

            // Bước 2: Parse String JSON thành List<Question>
            parseQuestions(jsonString);

            Log.d(TAG, "Load thành công: " + allQuestions.size() + " câu hỏi");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Lỗi load câu hỏi: " + e.getMessage());
            return false;
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

    private void parseQuestions(String jsonString) throws Exception {
        JSONObject root = new JSONObject(jsonString);
        JSONArray array = root.getJSONArray("questions");

        allQuestions.clear();

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);

            int id             = obj.getInt("id");
            String text        = obj.getString("question");
            int correctIndex   = obj.getInt("correct");
            int level          = obj.getInt("level");

            // Parse mảng options
            JSONArray optArr = obj.getJSONArray("options");
            String[] options = new String[4];
            for (int j = 0; j < 4; j++) {
                options[j] = optArr.getString(j);
            }

            allQuestions.add(new Question(id, text, options, correctIndex, level));
        }
    }

    // ── Lấy câu hỏi cho ván chơi ────────────────────────────────────────────
    public List<Question> getGameQuestions() {
        List<Question> easy   = filterByLevel(1, 5);
        List<Question> medium = filterByLevel(6, 10);
        List<Question> hard   = filterByLevel(11, 15);

        // Trộn ngẫu nhiên từng nhóm
        Collections.shuffle(easy);
        Collections.shuffle(medium);
        Collections.shuffle(hard);

        List<Question> gameQuestions = new ArrayList<>();
        gameQuestions.addAll(easy.subList(0, Math.min(5, easy.size())));
        gameQuestions.addAll(medium.subList(0, Math.min(5, medium.size())));
        gameQuestions.addAll(hard.subList(0, Math.min(5, hard.size())));

        // Bổ sung nếu chưa đủ 15 câu
        if (gameQuestions.size() < 15) {
            List<Question> remaining = new ArrayList<>(allQuestions);
            remaining.removeAll(gameQuestions);
            Collections.shuffle(remaining);
            int need = 15 - gameQuestions.size();
            gameQuestions.addAll(remaining.subList(0, Math.min(need, remaining.size())));
        }

        return gameQuestions;
    }
    private List<Question> filterByLevel(int from, int to) {
        List<Question> result = new ArrayList<>();
        for (Question q : allQuestions) {
            if (q.getLevel() >= from && q.getLevel() <= to) {
                result.add(q);
            }
        }
        return result;
    }

    public int getTotalQuestions() {
        return allQuestions.size();
    }
}
