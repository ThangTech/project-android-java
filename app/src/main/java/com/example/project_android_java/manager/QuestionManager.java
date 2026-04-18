package com.example.project_android_java.manager;

import android.content.Context;
import android.util.Log;

import com.example.project_android_java.database.DatabaseHelper;
import com.example.project_android_java.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionManager {

    private static final String TAG = "QuestionManager";

    private static QuestionManager instance;
    private Context context;
    private DatabaseHelper dbHelper;
    private List<Question> allQuestions;

    private QuestionManager(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = new DatabaseHelper(context);
        this.allQuestions = new ArrayList<>();
    }

    public static QuestionManager getInstance(Context context) {
        if (instance == null) {
            instance = new QuestionManager(context);
        }
        return instance;
    }

    public boolean loadQuestions() {
        try {
            dbHelper.importQuestionsFromJson(true);
            allQuestions = loadAllFromDatabase();

            if (allQuestions.isEmpty()) {
                Log.e(TAG, "Danh sách câu hỏi rỗng!");
                return false;
            }

            Log.d(TAG, "Load thành công: " + allQuestions.size() + " câu hỏi");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Lỗi load câu hỏi: " + e.getMessage());
            return false;
        }
    }

    public void refreshQuestions() {
        allQuestions = loadAllFromDatabase();
        Log.d(TAG, "Refresh thành công: " + allQuestions.size() + " câu hỏi");
    }

    private List<Question> loadAllFromDatabase() {
        List<Question> questions = new ArrayList<>();
        List<String[]> rawQuestions = dbHelper.getAllQuestions();

        for (String[] q : rawQuestions) {
            int id = Integer.parseInt(q[0]);
            String text = q[1];
            String[] options = {q[2], q[3], q[4], q[5]};
            int correct = Integer.parseInt(q[6]);
            int level = Integer.parseInt(q[7]);
            String category = q.length > 8 ? q[8] : "General";
            String evidence = q.length > 9 ? q[9] : "";

            questions.add(new Question(id, text, options, correct, level, category, evidence));
        }
        return questions;
    }

    public List<Question> getGameQuestions() {
        List<Question> easy = filterByLevel(1, 5);
        List<Question> medium = filterByLevel(6, 10);
        List<Question> hard = filterByLevel(11, 15);

        Collections.shuffle(easy);
        Collections.shuffle(medium);
        Collections.shuffle(hard);

        List<Question> gameQuestions = new ArrayList<>();
        gameQuestions.addAll(easy.subList(0, Math.min(5, easy.size())));
        gameQuestions.addAll(medium.subList(0, Math.min(5, medium.size())));
        gameQuestions.addAll(hard.subList(0, Math.min(5, hard.size())));

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

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}