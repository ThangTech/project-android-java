package com.example.project_android_java.model;

public class Question {
    private int id;
    private String questionText;
    private String[] options;
    private int correctAnswerIndex;
    private int level;
    private String category;

    public Question(int id, String questionText, String[] options,
                    int correctAnswerIndex, int level) {
        this(id, questionText, options, correctAnswerIndex, level, "General");
    }

    public Question(int id, String questionText, String[] options,
                    int correctAnswerIndex, int level, String category) {
        this.id = id;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.level = level;
        this.category = category;
    }
    public int getId()                  { return id; }
    public String getQuestionText()       { return questionText; }
    public String[] getOptions()        { return options; }
    public int getCorrectAnswerIndex()  { return correctAnswerIndex; }
    public int getLevel()               { return level; }
    public String getCategory()           { return category; }

    // ── Helper methods ───────────────────────────────────────────────────────

    /**
     * Kiểm tra người chơi chọn đúng không.
     * @param selectedIndex: index đáp án người chơi chọn (0, 1, 2, hoặc 3)
     * @return true nếu đúng, false nếu sai
     */
    public boolean isCorrect(int selectedIndex) {
        return selectedIndex == correctAnswerIndex;
    }

    /**
     * Lấy text của đáp án đúng.
     */
    public String getCorrectAnswerText() {
        return options[correctAnswerIndex];
    }
}