package com.example.project_android_java.manager;

import com.example.project_android_java.model.Question;
import com.example.project_android_java.ui.GameActivity;
import java.util.List;

public class GameManager {
    public enum State { PLAYING, WON, LOST }

    private final List<Question> questions;
    private int currentIndex = 0;
    private State state = State.PLAYING;

    public GameManager(List<Question> questions) {
        this.questions = questions;
    }

    public Question getCurrentQuestion() { return questions.get(currentIndex); }
    public int getCurrentIndex() { return currentIndex; }
    public State getState() { return state; }

    public boolean checkAnswer(int selectedIndex) {
        return getCurrentQuestion().isCorrect(selectedIndex);
    }

    public int getCorrectIndex() {
        return getCurrentQuestion().getCorrectAnswerIndex();
    }

    public void advance() {
        currentIndex++;
        if (currentIndex >= 15) state = State.WON;
    }

    public void setLost() {
        state = State.LOST;
    }

    /**
     * Tiền thưởng hiển thị trên màn hình chơi (số tiền chắc chắn có).
     * Câu 1 (index 0) -> 0đ.
     * Câu 2 (index 1) -> Tiền câu 1.
     */
    public long getSecureMoney() {
        if (currentIndex == 0) return 0L;
        return GameActivity.MONEY_LADDER[currentIndex - 1];
    }

    /**
     * Tiền thưởng thực nhận khi thua cuộc (Dựa trên mốc an toàn).
     */
    public long getMoneyEarned() {
        if (state == State.WON) return GameActivity.MONEY_LADDER[14];
        
        // Trả lời sai:
        // currentIndex là câu đang đứng mà sai (0-14 tương ứng câu 1-15)
        if (currentIndex >= 10) return GameActivity.MONEY_LADDER[9]; // Vượt qua mốc câu 10
        if (currentIndex >= 5)  return GameActivity.MONEY_LADDER[4]; // Vượt qua mốc câu 5
        return 0L;
    }

    // Các quyền trợ giúp...
    private boolean used5050 = false, usedPhone = false, usedAudience = false;
    public boolean isUsed5050() { return used5050; }
    public boolean isUsedPhone() { return usedPhone; }
    public boolean isUsedAudience() { return usedAudience; }
    public void use5050() { used5050 = true; }
    public void usePhone() { usedPhone = true; }
    public void useAudience() { usedAudience = true; }
}