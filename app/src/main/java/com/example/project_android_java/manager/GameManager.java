package com.example.project_android_java.manager;

import com.example.project_android_java.model.Question;
import com.example.project_android_java.ui.GameActivity;

import java.util.List;

/**
 * GAME MANAGER - Quản lý trạng thái và logic của một ván chơi.
 *
 * 📚 Kiến thức:
 *  - Tách logic ra class riêng giúp GameActivity gọn hơn
 *  - Enum State: đại diện trạng thái game (đang chơi / thắng / thua)
 *  - Single Responsibility Principle: mỗi class chỉ làm 1 việc
 */
public class GameManager {

    public enum State { PLAYING, WON, LOST }

    private final List<Question> questions;
    private int currentIndex = 0;
    private State state = State.PLAYING;

    public GameManager(List<Question> questions) {
        this.questions = questions;
    }

    // ── Truy cập trạng thái ───────────────────────────────────────────────────

    public Question getCurrentQuestion() { return questions.get(currentIndex); }
    public int getCurrentIndex()         { return currentIndex; }
    public State getState()              { return state; }
    public boolean isPlaying()           { return state == State.PLAYING; }

    // ── Kiểm tra đáp án (KHÔNG advance ngay) ─────────────────────────────────

    /**
     * Chỉ kiểm tra đúng/sai, KHÔNG thay đổi index.
     * GameActivity gọi advance() hoặc setLost() sau khi animation xong.
     */
    public boolean checkAnswer(int selectedIndex) {
        return getCurrentQuestion().isCorrect(selectedIndex);
    }

    public int getCorrectIndex() {
        return getCurrentQuestion().getCorrectAnswerIndex();
    }

    // ── Chuyển trạng thái ────────────────────────────────────────────────────

    /** Gọi sau khi người chơi trả lời ĐÚNG và animation xong. */
    public void advance() {
        currentIndex++;
        if (currentIndex >= 15) state = State.WON;
    }

    /** Gọi khi người chơi trả lời SAI. */
    public void setLost() {
        state = State.LOST;
    }

    // ── Tiền thưởng ──────────────────────────────────────────────────────────

    public long getMoneyEarned() {
        if (state == State.WON) return GameActivity.MONEY_LADDER[14];
        return getSafeMoney();
    }

    /**
     * Tính tiền nhận khi thua.
     * Trả về mốc an toàn gần nhất đã vượt qua.
     *  - Chưa qua câu 5  → 0đ
     *  - Qua câu 5-9     → 2.000.000đ
     *  - Qua câu 10+     → 22.000.000đ
     */
    private long getSafeMoney() {
        if (currentIndex >= 10) return GameActivity.MONEY_LADDER[9];
        if (currentIndex >= 5)  return GameActivity.MONEY_LADDER[4];
        return 0L;
    }

    // ── Mốc an toàn ──────────────────────────────────────────────────────────

    /**
     * index 4 = câu 5, index 9 = câu 10 (0-based).
     */
    public boolean isSafeCheckpoint(int index) {
        return index == 4 || index == 9;
    }

    // ── Quyền trợ giúp ───────────────────────────────────────────────────────
    // Mỗi lifeline chỉ dùng được 1 lần trong cả ván chơi.

    private boolean used5050     = false;
    private boolean usedPhone    = false;
    private boolean usedAudience = false;

    public boolean isUsed5050()     { return used5050; }
    public boolean isUsedPhone()    { return usedPhone; }
    public boolean isUsedAudience() { return usedAudience; }

    public void use5050()     { used5050     = true; }
    public void usePhone()    { usedPhone    = true; }
    public void useAudience() { usedAudience = true; }
}
