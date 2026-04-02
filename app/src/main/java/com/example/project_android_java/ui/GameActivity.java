package com.example.project_android_java.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_android_java.R;
import com.example.project_android_java.manager.GameManager;
import com.example.project_android_java.manager.QuestionManager;
import com.example.project_android_java.model.Question;

import java.util.List;

public class GameActivity extends AppCompatActivity {

    public static final long[] MONEY_LADDER = {
            200_000L, 400_000L, 600_000L, 1_000_000L, 2_000_000L,
            3_000_000L, 6_000_000L, 10_000_000L, 14_000_000L, 22_000_000L,
            30_000_000L, 40_000_000L, 60_000_000L, 85_000_000L, 150_000_000L
    };

    private View layoutQuestionMain, layoutMoneyLadder;
    private TextView tvQuestion, tvTimer;
    private Button[] answerButtons;
    private Button btnHideLadder;
    private LinearLayout llMoneyContainer;
    private TextView[] ladderViews = new TextView[15];

    private GameManager gameManager;
    private CountDownTimer countDownTimer;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initViews();
        initGame();
        buildMoneyLadder();
        
        // Mặc định ban đầu: Hiện bảng tiền thưởng, ẩn câu hỏi
        layoutMoneyLadder.setVisibility(View.VISIBLE);
        layoutQuestionMain.setVisibility(View.INVISIBLE);
    }

    private void initViews() {
        layoutQuestionMain = findViewById(R.id.layout_question_main);
        layoutMoneyLadder = findViewById(R.id.layout_money_ladder);
        tvQuestion = findViewById(R.id.tv_question);
        tvTimer = findViewById(R.id.tv_timer);
        btnHideLadder = findViewById(R.id.btn_hide_ladder);
        llMoneyContainer = findViewById(R.id.ll_money_container);

        answerButtons = new Button[]{
                findViewById(R.id.btn_answer_a),
                findViewById(R.id.btn_answer_b),
                findViewById(R.id.btn_answer_c),
                findViewById(R.id.btn_answer_d)
        };

        for (int i = 0; i < 4; i++) {
            final int index = i;
            answerButtons[i].setOnClickListener(v -> onAnswerSelected(index));
        }

        btnHideLadder.setOnClickListener(v -> showReadyDialog());
    }

    private void initGame() {
        QuestionManager qm = QuestionManager.getInstance(this);
        qm.loadQuestions();
        gameManager = new GameManager(qm.getGameQuestions());
    }

    private void buildMoneyLadder() {
        llMoneyContainer.removeAllViews();
        for (int i = 14; i >= 0; i--) {
            TextView tv = new TextView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dp(45));
            lp.setMargins(0, dp(4), 0, dp(4));
            tv.setLayoutParams(lp);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(16);
            tv.setText(formatMoney(MONEY_LADDER[i]));
            
            // Highlight các mốc quan trọng 5, 10, 15
            if (i == 4 || i == 9 || i == 14) {
                tv.setBackgroundResource(R.drawable.bg_ladder_safe);
                tv.setTextColor(Color.YELLOW);
            } else {
                tv.setTextColor(Color.WHITE);
            }
            
            ladderViews[i] = tv;
            llMoneyContainer.addView(tv);
        }
        updateLadderHighlight();
    }

    private void updateLadderHighlight() {
        int current = gameManager.getCurrentIndex();
        for (int i = 0; i < 15; i++) {
            if (i == current) {
                ladderViews[i].setScaleX(1.15f);
                ladderViews[i].setScaleY(1.15f);
                ladderViews[i].setTextColor(Color.CYAN); // Màu cho câu hiện tại
                // Thêm animation nhấp nháy cho mốc hiện tại
                Animation blink = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
                blink.setDuration(500);
                blink.setRepeatMode(Animation.REVERSE);
                blink.setRepeatCount(Animation.INFINITE);
                ladderViews[i].startAnimation(blink);
            } else {
                ladderViews[i].setScaleX(1f);
                ladderViews[i].setScaleY(1f);
                ladderViews[i].clearAnimation();
                if (i != 4 && i != 9 && i != 14) tvColor(ladderViews[i], Color.WHITE);
            }
        }
    }
    
    private void tvColor(TextView tv, int color) { tv.setTextColor(color); }

    private void showReadyDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Sẵn sàng?")
                .setMessage("Bạn đã sẵn sàng chơi với chúng tôi chưa?")
                .setCancelable(false)
                .setPositiveButton("Sẵn sàng", (dialog, which) -> startRealGame())
                .setNegativeButton("Bỏ qua", (dialog, which) -> finish())
                .show();
    }

    private void startRealGame() {
        // 1. Trượt màn hình mốc tiền sang phải
        layoutMoneyLadder.animate()
                .translationX(layoutMoneyLadder.getWidth())
                .setDuration(500)
                .withEndAction(() -> {
                    layoutMoneyLadder.setVisibility(View.GONE);
                    layoutQuestionMain.setVisibility(View.VISIBLE);
                    // 2. Bắt đầu câu hỏi đầu tiên
                    showQuestion();
                }).start();
    }

    private void showQuestion() {
        Question q = gameManager.getCurrentQuestion();
        tvQuestion.setText(q.getQuestionText());
        String[] options = q.getOptions();
        for (int i = 0; i < 4; i++) {
            answerButtons[i].setText(options[i]);
            answerButtons[i].setBackgroundResource(R.drawable.btn_hex_blue);
            answerButtons[i].setEnabled(true);
        }
        startTimer();
    }

    private void startTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        countDownTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("" + millisUntilFinished / 1000);
            }
            public void onFinish() {
                goToResult(false, gameManager.getMoneyEarned());
            }
        }.start();
    }

    private void onAnswerSelected(int selectedIndex) {
        if (countDownTimer != null) countDownTimer.cancel();
        setAllButtonsEnabled(false);
        answerButtons[selectedIndex].setBackgroundResource(R.drawable.btn_hex_orange);

        handler.postDelayed(() -> {
            if (gameManager.checkAnswer(selectedIndex)) {
                // TRẢ LỜI ĐÚNG
                onCorrect(selectedIndex);
            } else {
                // TRẢ LỜI SAI
                onWrong(selectedIndex);
            }
        }, 2000);
    }

    private void onCorrect(int selectedIndex) {
        blinkButton(answerButtons[selectedIndex], R.drawable.btn_hex_green, () -> {
            gameManager.advance();
            // HIỆN LẠI BẢNG TIỀN TRƯỚC KHI QUA CÂU MỚI
            showMoneyLadderBriefly();
        });
    }

    private void showMoneyLadderBriefly() {
        updateLadderHighlight();
        layoutMoneyLadder.setTranslationX(layoutMoneyLadder.getWidth());
        layoutMoneyLadder.setVisibility(View.VISIBLE);
        
        layoutMoneyLadder.animate()
                .translationX(0)
                .setDuration(500)
                .withEndAction(() -> {
                    handler.postDelayed(() -> {
                        layoutMoneyLadder.animate()
                                .translationX(layoutMoneyLadder.getWidth())
                                .setDuration(500)
                                .withEndAction(() -> {
                                    layoutMoneyLadder.setVisibility(View.GONE);
                                    if (gameManager.getState() == GameManager.State.WON) {
                                        goToResult(true, MONEY_LADDER[14]);
                                    } else {
                                        showQuestion();
                                    }
                                }).start();
                    }, 2000); // Dừng lại 2 giây cho người chơi xem mốc tiền
                }).start();
    }

    private void onWrong(int selectedIndex) {
        blinkButton(answerButtons[selectedIndex], R.drawable.btn_hex_red, () -> {
            int correct = gameManager.getCorrectIndex();
            answerButtons[correct].setBackgroundResource(R.drawable.btn_hex_green);
            handler.postDelayed(() -> goToResult(false, gameManager.getMoneyEarned()), 1500);
        });
    }

    private void blinkButton(Button btn, int targetDrawable, Runnable onFinished) {
        final int blue = R.drawable.btn_hex_blue;
        for (int i = 0; i < 6; i++) {
            final int idx = i;
            handler.postDelayed(() -> {
                btn.setBackgroundResource(idx % 2 == 0 ? targetDrawable : blue);
                if (idx == 5) {
                    btn.setBackgroundResource(targetDrawable);
                    if (onFinished != null) onFinished.run();
                }
            }, i * 200);
        }
    }

    private void goToResult(boolean win, long money) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("IS_WIN", win);
        intent.putExtra("MONEY_EARNED", money);
        startActivity(intent);
        finish();
    }

    private void setAllButtonsEnabled(boolean e) {
        for (Button b : answerButtons) b.setEnabled(e);
    }

    private int dp(int dp) { return Math.round(dp * getResources().getDisplayMetrics().density); }
    public static String formatMoney(long amount) { return String.format("%,d", amount).replace(',', '.') + " đ"; }
}