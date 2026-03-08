package com.example.project_android_java.model;

public class Question {
    private int id;
    private String questionText;       // Nội dung câu hỏi
    private String[] options;          // 4 đáp án: [A, B, C, D]
    private int correctAnswerIndex;    // Vị trí đáp án đúng: 0=A, 1=B, 2=C, 3=D
    private int level;                 // Độ khó: 1 (dễ nhất) → 15 (khó nhất)

    public Question(int id, String questionText, String[] options,
                    int correctAnswerIndex, int level) {
        this.id = id;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.level = level;
    }
    public int getId()                  { return id; }
    public String getQuestionText()     { return questionText; }
    public String[] getOptions()        { return options; }
    public int getCorrectAnswerIndex()  { return correctAnswerIndex; }
    public int getLevel()               { return level; }

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