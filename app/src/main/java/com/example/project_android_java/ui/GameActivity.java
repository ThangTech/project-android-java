package com.example.project_android_java.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_android_java.R;
import com.example.project_android_java.manager.GameManager;
import com.example.project_android_java.manager.QuestionManager;
import com.example.project_android_java.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * GAME ACTIVITY - Màn hình chơi game chính.
 *
 * 📚 Kiến thức:
 *  - GameManager: tách logic game ra class riêng (Single Responsibility)
 *  - ViewPropertyAnimator: animate View properties mượt mà
 *  - buildMoneyLadder(): tạo View động bằng code (không dùng XML cứng)
 *  - Handler.postDelayed(): delay action sau animation
 *  - AlertDialog.Builder: dựng dialog bằng code, setView() cho layout tuỳ chỉnh
 *  - ProgressBar (horizontal): hiển thị tỉ lệ % trong dialog khán giả
 *  - View.INVISIBLE vs View.GONE: INVISIBLE giữ nguyên vị trí, GONE thu hồi space
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

    // ── Lifeline buttons ──────────────────────────────────────────────────────
    private Button btnLifeline5050;
    private Button btnLifelonePhone;
    private Button btnLifeloneAudience;

    // ── Game State ────────────────────────────────────────────────────────────
    private GameManager gameManager;

    /**
     * Theo dõi nút nào đang bị ẩn do 50:50.
     * Reset về false khi chuyển sang câu mới.
     *
     * 📚 View.INVISIBLE: nút ẩn nhưng vẫn giữ chỗ trong layout
     *    (tránh layout nhảy so với View.GONE xoá hẳn space)
     */
    private boolean[] hiddenByFiftyFifty = new boolean[4];

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

        btnLifeline5050       = findViewById(R.id.btn_lifeline_5050);
        btnLifelonePhone      = findViewById(R.id.btn_lifeline_phone);
        btnLifeloneAudience   = findViewById(R.id.btn_lifeline_audience);

        btnLifeline5050.setOnClickListener(v -> onLifeline5050());
        btnLifelonePhone.setOnClickListener(v -> onLifelonePhone());
        btnLifeloneAudience.setOnClickListener(v -> onLifeloneAudience());
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
     */
    private void updateLadder() {
        int current = gameManager.getCurrentIndex();
        for (int i = 0; i < 15; i++) {
            if (i == current && gameManager.isPlaying()) {
                ladderViews[i].setBackgroundResource(R.drawable.bg_ladder_current);
                ladderViews[i].setTextColor(Color.BLACK);
            } else if (gameManager.isSafeCheckpoint(i)) {
                ladderViews[i].setBackgroundResource(R.drawable.bg_ladder_safe);
                ladderViews[i].setTextColor(i < current ? 0xFFAED581 : 0xFF4CAF50);
            } else {
                ladderViews[i].setBackgroundResource(R.drawable.bg_ladder_normal);
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
            answerButtons[i].setVisibility(View.VISIBLE);   // khôi phục nút 50:50 đã ẩn
            answerButtons[i].setBackgroundResource(R.drawable.btn_hex_normal);
            answerButtons[i].setScaleX(1f);
            answerButtons[i].setScaleY(1f);
        }

        // Reset trạng thái ẩn của 50:50 cho câu mới
        hiddenByFiftyFifty = new boolean[4];

        updateLadder();
        updateLifelinesUI();
    }

    // ── Quyền trợ giúp ───────────────────────────────────────────────────────

    /**
     * Cập nhật trạng thái 3 nút lifeline: đã dùng → disable + đổi màu xám.
     * Gọi sau mỗi lần dùng lifeline và sau mỗi lần chuyển câu.
     */
    private void updateLifelinesUI() {
        applyLifelineState(btnLifeline5050,     gameManager.isUsed5050());
        applyLifelineState(btnLifelonePhone,    gameManager.isUsedPhone());
        applyLifelineState(btnLifeloneAudience, gameManager.isUsedAudience());
    }

    private void applyLifelineState(Button btn, boolean used) {
        btn.setEnabled(!used);
        btn.setBackgroundResource(used ? R.drawable.btn_lifeline_used : R.drawable.btn_lifeline_normal);
        btn.setTextColor(used ? 0xFF444466 : Color.WHITE);
    }

    // ── 50:50 ─────────────────────────────────────────────────────────────────

    /**
     * Loại bỏ 2 đáp án sai ngẫu nhiên bằng cách đặt INVISIBLE.
     *
     * 📚 Collections.shuffle() trộn list index → chọn 2 phần tử đầu tiên
     *    là cách lấy ngẫu nhiên không trùng lặp ngắn gọn nhất.
     */
    private void onLifeline5050() {
        gameManager.use5050();
        applyLifelineState(btnLifeline5050, true);

        int correct = gameManager.getCorrectIndex();

        // Thu thập index các đáp án SAI
        List<Integer> wrongs = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i != correct) wrongs.add(i);
        }

        // Trộn và ẩn 2 đáp án đầu tiên
        Collections.shuffle(wrongs);
        for (int k = 0; k < 2; k++) {
            int idx = wrongs.get(k);
            hiddenByFiftyFifty[idx] = true;
            answerButtons[idx].setVisibility(View.INVISIBLE);
        }
    }

    // ── Gọi điện ──────────────────────────────────────────────────────────────

    /**
     * Gợi ý đáp án từ "người thân".
     *
     * Xác suất:
     *  - 80%: chỉ đúng đáp án, nói "khoảng 80% chắc chắn"
     *  - 20%: chỉ sai (trong số đáp án còn hiển thị), nói "không chắc lắm"
     *
     * 📚 AlertDialog.Builder: chuỗi builder pattern để dựng dialog.
     *    setMessage(): hiện text thuần.
     *    setPositiveButton(text, null): nút đóng, listener null = chỉ dismiss.
     */
    private void onLifelonePhone() {
        gameManager.usePhone();
        applyLifelineState(btnLifelonePhone, true);

        int correct = gameManager.getCorrectIndex();
        Random rnd  = new Random();

        int suggestedIdx;
        String note;

        if (rnd.nextInt(100) < 80) {
            suggestedIdx = correct;
            note = "Mình khoảng 80% chắc chắn đó!";
        } else {
            // Chọn ngẫu nhiên trong số đáp án SAI còn hiển thị
            List<Integer> visibleWrongs = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                if (i != correct && !hiddenByFiftyFifty[i]) visibleWrongs.add(i);
            }
            suggestedIdx = visibleWrongs.get(rnd.nextInt(visibleWrongs.size()));
            note = "Nhưng mình không chắc lắm...";
        }

        String[] labels = {"A", "B", "C", "D"};
        String msg = "\"Câu này mình nghĩ là đáp án "
                + labels[suggestedIdx] + ".\n" + note + "\"";

        new AlertDialog.Builder(this)
                .setTitle("Gọi điện cho người thân")
                .setMessage(msg)
                .setPositiveButton("Cảm ơn!", null)
                .show();
    }

    // ── Hỏi khán giả ─────────────────────────────────────────────────────────

    /**
     * Hiển thị % bình chọn của khán giả qua AlertDialog tuỳ chỉnh.
     * Đáp án đúng luôn nhận % cao nhất (50–70% nếu 4 options, 58–80% nếu còn 2 sau 50:50).
     *
     * 📚 AlertDialog.setView(): gắn layout tuỳ chỉnh thay cho setMessage().
     *    ProgressBar horizontal: dùng style progressBarStyleHorizontal.
     *    setProgressTintList(): đổi màu thanh tiến trình bằng ColorStateList.
     */
    private void onLifeloneAudience() {
        gameManager.useAudience();
        applyLifelineState(btnLifeloneAudience, true);

        int correct   = gameManager.getCorrectIndex();
        int[] percents = generateAudiencePercents(correct);

        showAudienceDialog(percents);
    }

    /**
     * Sinh % ngẫu nhiên cho 4 đáp án, đảm bảo tổng = 100.
     *
     * Nếu 50:50 đã dùng: chỉ chia giữa 2 đáp án còn hiển thị.
     * Nếu chưa dùng 50:50: chia giữa cả 4, đáp án đúng chiếm nhiều nhất.
     */
    private int[] generateAudiencePercents(int correct) {
        Random rnd    = new Random();
        int[]  result = new int[4];

        if (gameManager.isUsed5050()) {
            // Chỉ 2 đáp án còn hiển thị
            int correctPct = 58 + rnd.nextInt(23);   // 58–80%
            for (int i = 0; i < 4; i++) {
                if (hiddenByFiftyFifty[i]) continue;
                result[i] = (i == correct) ? correctPct : (100 - correctPct);
            }
        } else {
            // 4 đáp án: đúng chiếm 50–70%, 3 sai chia phần còn lại
            int correctPct = 50 + rnd.nextInt(21);   // 50–70%
            int remaining  = 100 - correctPct;
            // Chia remaining thành 3 phần ≥ 1
            int a = 1 + rnd.nextInt(remaining - 2);          // [1, remaining-2]
            int b = 1 + rnd.nextInt(remaining - a - 1);      // [1, remaining-a-1]
            int c = remaining - a - b;
            int[] wrongPct = {a, b, c};
            int j = 0;
            for (int i = 0; i < 4; i++) {
                result[i] = (i == correct) ? correctPct : wrongPct[j++];
            }
        }
        return result;
    }

    /**
     * Dựng dialog "Hỏi khán giả" hoàn toàn bằng code.
     * Mỗi hàng: [nhãn A/B/C/D] [ProgressBar] [% text].
     * Bỏ qua các đáp án đã bị ẩn bởi 50:50.
     */
    private void showAudienceDialog(int[] percents) {
        String[] labels = {"A", "B", "C", "D"};

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(dp(24), dp(16), dp(24), dp(8));

        for (int i = 0; i < 4; i++) {
            if (hiddenByFiftyFifty[i]) continue;

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(0, dp(6), 0, dp(6));

            // Nhãn A / B / C / D
            TextView tvLabel = new TextView(this);
            tvLabel.setText(labels[i]);
            tvLabel.setTextColor(0xFFFFD700);
            tvLabel.setTextSize(14);
            tvLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            tvLabel.setMinWidth(dp(20));

            // Thanh ProgressBar
            ProgressBar pb = new ProgressBar(
                    this, null, android.R.attr.progressBarStyleHorizontal);
            pb.setMax(100);
            pb.setProgress(percents[i]);
            pb.setProgressTintList(ColorStateList.valueOf(0xFF4A90D9));
            LinearLayout.LayoutParams pbParams = new LinearLayout.LayoutParams(
                    0, dp(14));
            pbParams.weight = 1;
            pbParams.setMargins(dp(8), 0, dp(8), 0);

            // Số phần trăm
            TextView tvPct = new TextView(this);
            tvPct.setText(percents[i] + "%");
            tvPct.setTextColor(Color.WHITE);
            tvPct.setTextSize(13);
            tvPct.setMinWidth(dp(36));
            tvPct.setGravity(Gravity.END);

            row.addView(tvLabel);
            row.addView(pb, pbParams);
            row.addView(tvPct);
            container.addView(row, rowParams);
        }

        new AlertDialog.Builder(this)
                .setTitle("Hỏi ý kiến khán giả")
                .setView(container)
                .setPositiveButton("Cảm ơn!", null)
                .show();
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

    /**
     * Disable tất cả nút đáp án VÀ lifeline khi đang xử lý animation.
     * Khi enable lại (qua showQuestion), lifeline được khôi phục qua updateLifelinesUI().
     */
    private void setAllButtonsEnabled(boolean enabled) {
        for (Button btn : answerButtons) btn.setEnabled(enabled);
        if (!enabled) {
            btnLifeline5050.setEnabled(false);
            btnLifelonePhone.setEnabled(false);
            btnLifeloneAudience.setEnabled(false);
        }
        // Khi enabled=true, showQuestion() sẽ gọi updateLifelinesUI() để khôi phục
    }

    /** Chuyển dp → px theo mật độ màn hình hiện tại. */
    private int dp(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
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
