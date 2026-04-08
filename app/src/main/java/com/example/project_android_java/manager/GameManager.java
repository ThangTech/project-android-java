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

    public long getSecureMoney() {
        long total = 0;
        for (int i = 0; i < currentIndex; i++) {
            total += GameActivity.MONEY_LADDER[i];
        }
        return total;
    }

    public long getMoneyEarned() {
        if (state == State.WON) return GameActivity.MONEY_LADDER[14];
        if (currentIndex >= 10) return GameActivity.MONEY_LADDER[9];
        if (currentIndex >= 5)  return GameActivity.MONEY_LADDER[4];
        return 0L;
    }

    // ── Lifelines ───────────────────────────────────────────────────────────
    private boolean used5050 = false, usedPhone = false, usedAudience = false, usedChange = false;

    public boolean isUsed5050() { return used5050; }
    public boolean isUsedPhone() { return usedPhone; }
    public boolean isUsedAudience() { return usedAudience; }
    public boolean isUsedChange() { return usedChange; }

    public void use5050() { used5050 = true; }
    public void usePhone() { usedPhone = true; }
    public void useAudience() { usedAudience = true; }
    public void useChange() { usedChange = true; }

    /**
     * Thực hiện đổi câu hỏi: Hoán đổi câu hỏi hiện tại với một câu hỏi ngẫu nhiên khác
     * (Trong thực tế, bạn có thể chỉ cần lấy câu tiếp theo trong list nếu list đủ dài)
     */
    public void changeQuestion(Question newQuestion) {
        questions.set(currentIndex, newQuestion);
    }
}