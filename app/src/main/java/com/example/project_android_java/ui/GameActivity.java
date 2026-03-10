package com.example.project_android_java.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_android_java.R;
import com.example.project_android_java.manager.GameManager;
import com.example.project_android_java.manager.QuestionManager;
import com.example.project_android_java.model.Question;

import java.util.List;

/**
 * GAME ACTIVITY - Màn hình chơi game chính.
 *
 * 📚 Kiến thức:
 *  - GameManager: tách logic game ra class riêng (Single Responsibility)
 *  - ViewPropertyAnimator: animate View properties mượt mà
 *  - buildMoneyLadder(): tạo View động bằng code (không dùng XML cứng)
 *  - Handler.postDelayed(): delay action sau animation
 */
public class GameActivity extends AppCompatActivity {

    // ── Mốc tiền thưởng 15 câu ───────────────────────────────────────────────
    public static final long[] MONEY_LADDER = {
            200_000L,       // Câu 1
            400_000L,       // Câu 2
            600_000L,       // Câu 3
            1_000_000L,     // Câu 4
            2_000_000L,     // Câu 5  ← Mốc an toàn 1
            3_000_000L,     // Câu 6
            6_000_000L,     // Câu 7
            10_000_000L,    // Câu 8
            14_000_000L,    // Câu 9
            22_000_000L,    // Câu 10 ← Mốc an toàn 2
            30_000_000L,    // Câu 11
            40_000_000L,    // Câu 12
            60_000_000L,    // Câu 13
            85_000_000L,    // Câu 14
            150_000_000L    // Câu 15
    };

    // ── Views ─────────────────────────────────────────────────────────────────
    private TextView tvQuestion;
    private TextView tvQuestionNumber;
    private TextView tvCurrentMoney;
    private Button[] answerButtons;
    private TextView[] ladderViews;   // 15 ô trong thanh mốc tiền

    // ── Game State ────────────────────────────────────────────────────────────
    private GameManager gameManager;

    private static final String[] OPTION_LABELS = {"A. ", "B. ", "C. ", "D. "};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initViews();
        initGame();
        buildMoneyLadder();
        showQuestion();
    }

    // ── Khởi tạo ─────────────────────────────────────────────────────────────

    private void initViews() {
        tvQuestion       = findViewById(R.id.tv_question);
        tvQuestionNumber = findViewById(R.id.tv_question_number);
        tvCurrentMoney   = findViewById(R.id.tv_current_money);

        answerButtons = new Button[]{
                findViewById(R.id.btn_answer_a),
                findViewById(R.id.btn_answer_b),
                findViewById(R.id.btn_answer_c),
                findViewById(R.id.btn_answer_d)
        };

        for (int i = 0; i < answerButtons.length; i++) {
            final int index = i;
            answerButtons[i].setOnClickListener(v -> onAnswerSelected(index));
        }
    }

    private void initGame() {
        QuestionManager qm = QuestionManager.getInstance(this);
        qm.loadQuestions();
        List<Question> questions = qm.getGameQuestions();
        gameManager = new GameManager(questions);
    }

    // ── Thanh mốc tiền ───────────────────────────────────────────────────────

    /**
     * Tạo 15 ô mốc tiền trong panel bên phải bằng code.
     * Hiển thị từ câu 15 (trên cùng) → câu 1 (dưới cùng) như game thật.
     *
     * 📚 Tạo View bằng code: new TextView(this), addView()
     *    Thay vì khai báo cứng 15 item trong XML (dài và khó maintain).
     */
    private void buildMoneyLadder() {
        LinearLayout container = findViewById(R.id.ll_money_ladder);
        container.removeAllViews();
        ladderViews = new TextView[15];

        for (int i = 14; i >= 0; i--) {
            TextView tv = new TextView(this);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(6, 2, 6, 2);
            tv.setLayoutParams(lp);

            tv.setPadding(6, 8, 6, 8);
            tv.setTextSize(10.5f);
            tv.setGravity(Gravity.CENTER);

            // ★ cho mốc an toàn, số thường cho câu bình thường
            String label = gameManager.isSafeCheckpoint(i)
                    ? "★" + (i + 1)
                    : String.valueOf(i + 1);
            tv.setText(label + "\n" + formatMoneyShort(MONEY_LADDER[i]));

            ladderViews[i] = tv;
            container.addView(tv);
        }

        updateLadder();
    }

    /**
     * Cập nhật màu sắc các ô mốc tiền theo câu hiện tại.
     *
     * 3 trạng thái:
     *  - Câu đang chơi   → vàng (nổi bật)
     *  - Mốc an toàn     → xanh lá
     *  - Câu thường      → tím tối (đã qua thì mờ hơn)
     */
    private void updateLadder() {
        int current = gameManager.getCurrentIndex();
        for (int i = 0; i < 15; i++) {
            if (i == current && gameManager.isPlaying()) {
                ladderViews[i].setBackgroundResource(R.drawable.bg_ladder_current);
                ladderViews[i].setTextColor(Color.BLACK);
            } else if (gameManager.isSafeCheckpoint(i)) {
                ladderViews[i].setBackgroundResource(R.drawable.bg_ladder_safe);
                // Đã qua mốc an toàn → màu nhạt hơn
                ladderViews[i].setTextColor(i < current ? 0xFFAED581 : 0xFF4CAF50);
            } else {
                ladderViews[i].setBackgroundResource(R.drawable.bg_ladder_normal);
                // Đã qua → mờ, chưa đến → trắng
                ladderViews[i].setTextColor(i < current ? 0xFF555577 : Color.WHITE);
            }
        }
    }

    // ── Hiển thị câu hỏi ─────────────────────────────────────────────────────

    private void showQuestion() {
        Question q   = gameManager.getCurrentQuestion();
        int idx      = gameManager.getCurrentIndex();

        tvQuestionNumber.setText("Câu " + (idx + 1) + " / 15");
        tvCurrentMoney.setText(formatMoney(MONEY_LADDER[idx]));
        tvQuestion.setText(q.getQuestionText());

        String[] options = q.getOptions();
        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i].setText(OPTION_LABELS[i] + options[i]);
            answerButtons[i].setEnabled(true);
            answerButtons[i].setBackgroundResource(R.drawable.btn_hex_normal);
            // Reset scale phòng khi còn dư từ animation trước
            answerButtons[i].setScaleX(1f);
            answerButtons[i].setScaleY(1f);
        }

        updateLadder();
    }

    // ── Xử lý chọn đáp án ────────────────────────────────────────────────────

    private void onAnswerSelected(int selectedIndex) {
        setAllButtonsEnabled(false);

        boolean correct = gameManager.checkAnswer(selectedIndex);
        if (correct) {
            onCorrectAnswer(selectedIndex);
        } else {
            onWrongAnswer(selectedIndex);
        }
    }

    private void onCorrectAnswer(int selectedIndex) {
        animateButton(answerButtons[selectedIndex], true);

        new android.os.Handler().postDelayed(() -> {
            gameManager.advance();
            if (gameManager.getState() == GameManager.State.WON) {
                goToResult(true, MONEY_LADDER[14]);
            } else {
                showQuestion();
            }
        }, 1500);
    }

    private void onWrongAnswer(int selectedIndex) {
        animateButton(answerButtons[selectedIndex], false);

        // Sau 300ms mới hiện đáp án đúng để người chơi thấy rõ nút sai trước
        int correctIdx = gameManager.getCorrectIndex();
        new android.os.Handler().postDelayed(() ->
                answerButtons[correctIdx].setBackgroundResource(R.drawable.btn_hex_correct),
        300);

        gameManager.setLost();
        long moneyEarned = gameManager.getMoneyEarned();

        new android.os.Handler().postDelayed(() ->
                goToResult(false, moneyEarned),
        2000);
    }

    // ── Animation ─────────────────────────────────────────────────────────────

    /**
     * Hiệu ứng pulse khi chọn đáp án: phóng to nhẹ → thu về → đổi màu.
     *
     * 📚 ViewPropertyAnimator (btn.animate()):
     *  - API fluent, dễ đọc hơn ObjectAnimator
     *  - withEndAction(): callback sau khi animation kết thúc
     *  - Chaining: .scaleX().scaleY().setDuration() trên cùng 1 dòng
     */
    private void animateButton(Button btn, boolean correct) {
        int drawableRes = correct ? R.drawable.btn_hex_correct : R.drawable.btn_hex_wrong;

        btn.animate()
                .scaleX(1.07f)
                .scaleY(1.07f)
                .setDuration(120)
                .withEndAction(() -> {
                    btn.setBackgroundResource(drawableRes);
                    btn.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(120)
                            .start();
                }).start();
    }

    // ── Điều hướng ───────────────────────────────────────────────────────────

    private void goToResult(boolean isWin, long money) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("IS_WIN", isWin);
        intent.putExtra("MONEY_EARNED", money);
        intent.putExtra("QUESTION_REACHED", gameManager.getCurrentIndex() + 1);
        startActivity(intent);
        finish();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void setAllButtonsEnabled(boolean enabled) {
        for (Button btn : answerButtons) btn.setEnabled(enabled);
    }

    public static String formatMoney(long amount) {
        return String.format("%,d đ", amount).replace(',', '.');
    }

    /** Format gọn cho thanh mốc tiền: 150000000 → "150tr", 200000 → "200K" */
    private String formatMoneyShort(long amount) {
        if (amount >= 1_000_000) return (amount / 1_000_000) + "tr";
        return (amount / 1_000) + "K";
    }
}
