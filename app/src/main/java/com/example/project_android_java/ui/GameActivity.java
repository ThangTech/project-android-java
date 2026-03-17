package com.example.project_android_java.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class GameActivity extends AppCompatActivity {

    public static final long[] MONEY_LADDER = {
            200_000L, 400_000L, 600_000L, 1_000_000L, 2_000_000L,
            3_000_000L, 6_000_000L, 10_000_000L, 14_000_000L, 22_000_000L,
            30_000_000L, 40_000_000L, 60_000_000L, 85_000_000L, 150_000_000L
    };

    private TextView tvQuestion, tvQuestionNumber, tvCurrentMoney;
    private Button[] answerButtons;
    private TextView[] ladderViews;
    private Button btnLifeline5050, btnLifelonePhone, btnLifeloneAudience;

    private GameManager gameManager;
    private boolean[] hiddenByFiftyFifty = new boolean[4];
    private static final String[] OPTION_LABELS = {"A. ", "B. ", "C. ", "D. "};
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initViews();
        initGame();
        buildMoneyLadder();
        showQuestion();
    }

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

    private void buildMoneyLadder() {
        LinearLayout container = findViewById(R.id.ll_money_ladder);
        container.removeAllViews();
        ladderViews = new TextView[15];
        for (int i = 14; i >= 0; i--) {
            TextView tv = new TextView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(6, 2, 6, 2);
            tv.setLayoutParams(lp);
            tv.setPadding(6, 8, 6, 8);
            tv.setTextSize(10.5f);
            tv.setGravity(Gravity.CENTER);
            String label = gameManager.isSafeCheckpoint(i) ? "★" + (i + 1) : String.valueOf(i + 1);
            tv.setText(label + "\n" + formatMoneyShort(MONEY_LADDER[i]));
            ladderViews[i] = tv;
            container.addView(tv);
        }
        updateLadder();
    }

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

    private void showQuestion() {
        Question q = gameManager.getCurrentQuestion();
        int idx = gameManager.getCurrentIndex();

        tvQuestionNumber.setText("Câu " + (idx + 1) + " / 15");
        tvCurrentMoney.setText(formatMoney(MONEY_LADDER[idx]));
        tvQuestion.setText(q.getQuestionText());

        String[] options = q.getOptions();
        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i].setText(OPTION_LABELS[i] + options[i]);
            answerButtons[i].setEnabled(true);
            answerButtons[i].setVisibility(View.VISIBLE);
            answerButtons[i].setBackgroundResource(R.drawable.btn_hex_blue);
        }

        hiddenByFiftyFifty = new boolean[4];
        updateLadder();
        updateLifelinesUI();
    }

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

    private void onLifeline5050() {
        gameManager.use5050();
        applyLifelineState(btnLifeline5050, true);
        int correct = gameManager.getCorrectIndex();
        List<Integer> wrongs = new ArrayList<>();
        for (int i = 0; i < 4; i++) if (i != correct) wrongs.add(i);
        Collections.shuffle(wrongs);
        for (int k = 0; k < 2; k++) {
            int idx = wrongs.get(k);
            hiddenByFiftyFifty[idx] = true;
            answerButtons[idx].setVisibility(View.INVISIBLE);
        }
    }

    private void onLifelonePhone() {
        gameManager.usePhone();
        applyLifelineState(btnLifelonePhone, true);
        int correct = gameManager.getCorrectIndex();
        Random rnd = new Random();
        int suggestedIdx;
        String note;
        if (rnd.nextInt(100) < 80) {
            suggestedIdx = correct;
            note = "Mình khoảng 80% chắc chắn đó!";
        } else {
            List<Integer> visibleWrongs = new ArrayList<>();
            for (int i = 0; i < 4; i++) if (i != correct && !hiddenByFiftyFifty[i]) visibleWrongs.add(i);
            suggestedIdx = visibleWrongs.get(rnd.nextInt(visibleWrongs.size()));
            note = "Nhưng mình không chắc lắm...";
        }
        String[] labels = {"A", "B", "C", "D"};
        new AlertDialog.Builder(this).setTitle("Gọi điện cho người thân")
                .setMessage("\"Câu này mình nghĩ là đáp án " + labels[suggestedIdx] + ".\n" + note + "\"")
                .setPositiveButton("Cảm ơn!", null).show();
    }

    private void onLifeloneAudience() {
        gameManager.useAudience();
        applyLifelineState(btnLifeloneAudience, true);
        int correct = gameManager.getCorrectIndex();
        int[] percents = generateAudiencePercents(correct);
        showAudienceDialog(percents);
    }

    private int[] generateAudiencePercents(int correct) {
        Random rnd = new Random();
        int[] result = new int[4];
        if (gameManager.isUsed5050()) {
            int correctPct = 58 + rnd.nextInt(23);
            for (int i = 0; i < 4; i++) {
                if (hiddenByFiftyFifty[i]) continue;
                result[i] = (i == correct) ? correctPct : (100 - correctPct);
            }
        } else {
            int correctPct = 50 + rnd.nextInt(21);
            int remaining = 100 - correctPct;
            int a = 1 + rnd.nextInt(remaining - 2);
            int b = 1 + rnd.nextInt(remaining - a - 1);
            int c = remaining - a - b;
            int[] wrongPct = {a, b, c};
            int j = 0;
            for (int i = 0; i < 4; i++) result[i] = (i == correct) ? correctPct : wrongPct[j++];
        }
        return result;
    }

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
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(0, dp(6), 0, dp(6));
            TextView tvLabel = new TextView(this);
            tvLabel.setText(labels[i]);
            tvLabel.setTextColor(0xFFFFD700);
            tvLabel.setTextSize(14);
            tvLabel.setMinWidth(dp(20));
            ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            pb.setMax(100);
            pb.setProgress(percents[i]);
            pb.setProgressTintList(ColorStateList.valueOf(0xFF4A90D9));
            LinearLayout.LayoutParams pbParams = new LinearLayout.LayoutParams(0, dp(14));
            pbParams.weight = 1;
            pbParams.setMargins(dp(8), 0, dp(8), 0);
            TextView tvPct = new TextView(this);
            tvPct.setText(percents[i] + "%");
            tvPct.setTextColor(Color.WHITE);
            tvPct.setGravity(Gravity.END);
            row.addView(tvLabel);
            row.addView(pb, pbParams);
            row.addView(tvPct);
            container.addView(row, rowParams);
        }
        new AlertDialog.Builder(this).setTitle("Hỏi ý kiến khán giả").setView(container)
                .setPositiveButton("Cảm ơn!", null).show();
    }

    private void onAnswerSelected(int selectedIndex) {
        setAllButtonsEnabled(false);
        // Khi chọn: chuyển sang màu cam
        answerButtons[selectedIndex].setBackgroundResource(R.drawable.btn_hex_orange);

        handler.postDelayed(() -> {
            boolean correct = gameManager.checkAnswer(selectedIndex);
            if (correct) {
                blinkButton(answerButtons[selectedIndex], R.drawable.btn_hex_green, () -> {
                    gameManager.advance();
                    if (gameManager.getState() == GameManager.State.WON) {
                        goToResult(true, MONEY_LADDER[14]);
                    } else {
                        showQuestion();
                    }
                });
            } else {
                blinkButton(answerButtons[selectedIndex], R.drawable.btn_hex_red, () -> {
                    int correctIdx = gameManager.getCorrectIndex();
                    answerButtons[correctIdx].setBackgroundResource(R.drawable.btn_hex_green);
                    gameManager.setLost();
                    handler.postDelayed(() -> goToResult(false, gameManager.getMoneyEarned()), 1500);
                });
            }
        }, 2000); // Đợi 2 giây "hồi hộp" trước khi công bố đáp án
    }

    /**
     * Hiệu ứng nhấp nháy: đổi màu qua lại giữa màu mục tiêu và màu mặc định.
     */
    private void blinkButton(Button btn, int targetDrawable, Runnable onFinished) {
        final int defaultDrawable = R.drawable.btn_hex_blue;
        final int flashCount = 6; // 3 lần bật, 3 lần tắt
        final long delay = 200;

        for (int i = 0; i < flashCount; i++) {
            final int index = i;
            handler.postDelayed(() -> {
                if (index % 2 == 0) {
                    btn.setBackgroundResource(targetDrawable);
                } else {
                    btn.setBackgroundResource(defaultDrawable);
                }
                if (index == flashCount - 1) {
                    btn.setBackgroundResource(targetDrawable); // Kết thúc ở màu mục tiêu
                    if (onFinished != null) onFinished.run();
                }
            }, i * delay);
        }
    }

    private void goToResult(boolean isWin, long money) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("IS_WIN", isWin);
        intent.putExtra("MONEY_EARNED", money);
        intent.putExtra("QUESTION_REACHED", gameManager.getCurrentIndex() + 1);
        startActivity(intent);
        finish();
    }

    private void setAllButtonsEnabled(boolean enabled) {
        for (Button btn : answerButtons) btn.setEnabled(enabled);
        if (!enabled) {
            btnLifeline5050.setEnabled(false);
            btnLifelonePhone.setEnabled(false);
            btnLifeloneAudience.setEnabled(false);
        }
    }

    private int dp(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    public static String formatMoney(long amount) {
        return String.format("%,d đ", amount).replace(',', '.');
    }

    private String formatMoneyShort(long amount) {
        if (amount >= 1_000_000) return (amount / 1_000_000) + "tr";
        return (amount / 1_000) + "K";
    }
}
