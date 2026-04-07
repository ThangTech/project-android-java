package com.example.project_android_java.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_android_java.R;
import com.example.project_android_java.manager.GameManager;
import com.example.project_android_java.manager.QuestionManager;
import com.example.project_android_java.model.Question;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    public static final long[] MONEY_LADDER = {
            200_000L, 400_000L, 600_000L, 1_000_000L, 2_000_000L,
            3_000_000L, 6_000_000L, 10_000_000L, 14_000_000L, 22_000_000L,
            30_000_000L, 40_000_000L, 60_000_000L, 85_000_000L, 150_000_000L
    };

    private View layoutQuestionMain, layoutMoneyLadder;
    private TextView tvQuestion, tvTimer, tvCurrentMoney, tvQuestionHeader;
    private ProgressBar pbTimer;
    private Button[] answerButtons;
    private Button btnHideLadder;
    private ImageButton btnStopGame, btnHelp5050, btnHelpAudience, btnHelpPhone;
    private LinearLayout llMoneyContainer;
    private TextView[] ladderViews = new TextView[15];

    private GameManager gameManager;
    private CountDownTimer countDownTimer;
    private final Handler handler = new Handler();
    private boolean isAnswerProcessing = false; 
    private final String[] OPTION_LABELS = {"A. ", "B. ", "C. ", "D. "};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initViews();
        initGame();
        buildMoneyLadder();
        
        layoutMoneyLadder.setVisibility(View.VISIBLE);
        layoutQuestionMain.setVisibility(View.INVISIBLE);
        btnHideLadder.setVisibility(View.VISIBLE);
    }

    private void initViews() {
        layoutQuestionMain = findViewById(R.id.layout_question_main);
        layoutMoneyLadder = findViewById(R.id.layout_money_ladder);
        tvQuestion = findViewById(R.id.tv_question);
        tvTimer = findViewById(R.id.tv_timer);
        pbTimer = findViewById(R.id.pb_timer);
        tvCurrentMoney = findViewById(R.id.tv_current_money);
        tvQuestionHeader = findViewById(R.id.tv_question_header);
        btnHideLadder = findViewById(R.id.btn_hide_ladder);
        btnStopGame = findViewById(R.id.btn_stop_game);
        
        btnHelp5050 = findViewById(R.id.btn_help_5050);
        btnHelpAudience = findViewById(R.id.btn_help_audience);
        btnHelpPhone = findViewById(R.id.btn_help_phone);

        llMoneyContainer = findViewById(R.id.ll_money_container);

        answerButtons = new Button[]{
                findViewById(R.id.btn_answer_a),
                findViewById(R.id.btn_answer_b),
                findViewById(R.id.btn_answer_c),
                findViewById(R.id.btn_answer_d)
        };

        for (int i = 0; i < 4; i++) {
            final int index = i;
            answerButtons[i].setBackgroundTintList(null);
            answerButtons[i].setOnClickListener(v -> onAnswerSelected(index));
        }

        btnHideLadder.setOnClickListener(v -> showReadyDialog());
        btnStopGame.setOnClickListener(v -> showStopGameDialog());
        
        btnHelp5050.setOnClickListener(v -> onHelp5050());
        btnHelpAudience.setOnClickListener(v -> onHelpAudience());
        btnHelpPhone.setOnClickListener(v -> onHelpCall());
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
                    LinearLayout.LayoutParams.MATCH_PARENT, dp(40));
            lp.setMargins(dp(30), dp(4), dp(30), dp(4));
            tv.setLayoutParams(lp);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(17);
            tv.setText(formatMoney(MONEY_LADDER[i]));
            
            if (i == 4 || i == 9 || i == 14) {
                tv.setBackgroundResource(R.drawable.bg_ladder_safe);
                tv.setTextColor(Color.YELLOW);
                tv.setTypeface(null, Typeface.BOLD);
            } else {
                tv.setTextColor(Color.WHITE);
                tv.setTypeface(null, Typeface.NORMAL);
            }
            ladderViews[i] = tv;
            llMoneyContainer.addView(tv);
        }
        updateLadderHighlight();
    }

    private void updateLadderHighlight() {
        int current = gameManager.getCurrentIndex();
        int highlightIndex = Math.min(current, 14);

        for (int i = 0; i < 15; i++) {
            ladderViews[i].clearAnimation();
            if (i == highlightIndex) {
                ladderViews[i].setBackgroundResource(R.drawable.bg_ladder_current);
                ladderViews[i].setTextColor(Color.WHITE);
                AlphaAnimation blink = new AlphaAnimation(0.4f, 1.0f);
                blink.setDuration(400);
                blink.setRepeatMode(Animation.REVERSE);
                blink.setRepeatCount(Animation.INFINITE);
                ladderViews[i].startAnimation(blink);
            } else {
                if (i == 4 || i == 9 || i == 14) {
                    ladderViews[i].setBackgroundResource(R.drawable.bg_ladder_safe);
                    ladderViews[i].setTextColor(Color.YELLOW);
                } else {
                    ladderViews[i].setBackgroundResource(0);
                    ladderViews[i].setTextColor(Color.WHITE);
                }
            }
        }
    }

    private void showReadyDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Sẵn sàng?")
                .setMessage("Bạn đã sẵn sàng chơi với chúng tôi chưa?")
                .setCancelable(false)
                .setPositiveButton("Sẵn sàng", (dialog, which) -> startRealGame())
                .setNegativeButton("Bỏ qua", (dialog, which) -> finish())
                .show();
    }

    private void showStopGameDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Dừng cuộc chơi?")
                .setMessage("Nếu dừng lại bây giờ, điểm số của bạn sẽ không được lưu vào bảng xếp hạng. Bạn có chắc chắn muốn thoát?")
                .setPositiveButton("Dừng", (dialog, which) -> finish())
                .setNegativeButton("Chơi tiếp", null)
                .show();
    }

    private void startRealGame() {
        btnHideLadder.setVisibility(View.GONE);
        layoutMoneyLadder.animate()
                .translationX(layoutMoneyLadder.getWidth())
                .setDuration(600)
                .withEndAction(() -> {
                    layoutMoneyLadder.setVisibility(View.GONE);
                    layoutQuestionMain.setVisibility(View.VISIBLE);
                    showQuestion();
                }).start();
    }

    private void showQuestion() {
        isAnswerProcessing = false; 
        Question q = gameManager.getCurrentQuestion();
        tvQuestion.setText(q.getQuestionText());
        String[] options = q.getOptions();
        for (int i = 0; i < 4; i++) {
            answerButtons[i].setText(OPTION_LABELS[i] + options[i]);
            answerButtons[i].setBackgroundResource(R.drawable.bg_answer_clear);
            answerButtons[i].setBackgroundTintList(null); 
            answerButtons[i].setVisibility(View.VISIBLE);
        }
        
        tvQuestionHeader.setText("CÂU HỎI SỐ: " + (gameManager.getCurrentIndex() + 1));
        tvCurrentMoney.setText(formatMoney(gameManager.getSecureMoney()));
        
        startTimer();
    }

    private void startTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        countDownTimer = new CountDownTimer(30000, 100) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                tvTimer.setText(String.valueOf(seconds));
                pbTimer.setProgress(seconds);
            }
            public void onFinish() {
                pbTimer.setProgress(0);
                onWrong(-1);
            }
        }.start();
    }

    private void onAnswerSelected(int selectedIndex) {
        if (isAnswerProcessing) return; 
        isAnswerProcessing = true;

        if (countDownTimer != null) countDownTimer.cancel();
        
        answerButtons[selectedIndex].setBackgroundResource(R.drawable.btn_hex_blue);
        answerButtons[selectedIndex].setBackgroundTintList(null);

        handler.postDelayed(() -> {
            if (gameManager.checkAnswer(selectedIndex)) {
                onCorrect(selectedIndex);
            } else {
                onWrong(selectedIndex);
            }
        }, 2000);
    }

    private void onCorrect(int selectedIndex) {
        blinkButton(answerButtons[selectedIndex], R.drawable.btn_hex_green, R.drawable.btn_hex_blue, () -> {
            gameManager.advance(); 
            showMoneyLadderBriefly();
        });
    }

    private void onWrong(int selectedIndex) {
        if (selectedIndex != -1) {
            blinkButton(answerButtons[selectedIndex], R.drawable.btn_hex_red, R.drawable.btn_hex_blue, () -> {
                int correct = gameManager.getCorrectIndex();
                answerButtons[correct].setBackgroundResource(R.drawable.btn_hex_green);
                answerButtons[correct].setBackgroundTintList(null);
                finishGameWithMoney();
            });
        } else {
            finishGameWithMoney();
        }
    }

    private void finishGameWithMoney() {
        gameManager.setLost();
        long money = gameManager.getMoneyEarned();
        handler.postDelayed(() -> goToResult(false, money), 1500);
    }

    private void showMoneyLadderBriefly() {
        updateLadderHighlight();
        
        layoutMoneyLadder.setTranslationX(layoutMoneyLadder.getWidth());
        layoutMoneyLadder.setVisibility(View.VISIBLE);
        layoutMoneyLadder.animate().translationX(0).setDuration(600).withEndAction(() -> {
            handler.postDelayed(() -> {
                layoutMoneyLadder.animate().translationX(layoutMoneyLadder.getWidth()).setDuration(600).withEndAction(() -> {
                    layoutMoneyLadder.setVisibility(View.GONE);
                    
                    if (gameManager.getState() == GameManager.State.WON) {
                        goToResult(true, MONEY_LADDER[14]);
                    } else {
                        showQuestion();
                    }
                }).start();
            }, 2500);
        }).start();
    }

    private void blinkButton(Button btn, int targetDrawable, int selectedDrawable, Runnable onFinished) {
        for (int i = 0; i < 6; i++) {
            final int idx = i;
            handler.postDelayed(() -> {
                btn.setBackgroundResource(idx % 2 == 0 ? targetDrawable : selectedDrawable);
                btn.setBackgroundTintList(null);
                if (idx == 5) {
                    btn.setBackgroundResource(targetDrawable);
                    btn.setBackgroundTintList(null);
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

    // ── Quyền trợ giúp ───────────────────────────────────────────────────────────

    private void onHelp5050() {
        if (gameManager.isUsed5050()) return;
        gameManager.use5050();
        btnHelp5050.setAlpha(0.4f);
        btnHelp5050.setEnabled(false);

        int correct = gameManager.getCorrectIndex();
        int removedCount = 0;
        Random rnd = new Random();
        while (removedCount < 2) {
            int idx = rnd.nextInt(4);
            if (idx != correct && answerButtons[idx].getVisibility() == View.VISIBLE) {
                answerButtons[idx].setVisibility(View.INVISIBLE);
                removedCount++;
            }
        }
    }

    private void onHelpAudience() {
        if (gameManager.isUsedAudience()) return;
        gameManager.useAudience();
        btnHelpAudience.setAlpha(0.4f);
        btnHelpAudience.setEnabled(false);
    }

    private void onHelpCall() {
        if (gameManager.isUsedPhone()) return;

        View helpView = LayoutInflater.from(this).inflate(R.layout.dialog_call_help, null);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(helpView).create();

        helpView.findViewById(R.id.ll_expert_cu_trong_xoay).setOnClickListener(v -> showCallResult("Cù Trọng Xoay", R.drawable.cu_trong_xoay, dialog));
        helpView.findViewById(R.id.ll_expert_truong_anh_ngoc).setOnClickListener(v -> showCallResult("Trương Anh Ngọc", R.drawable.truong_anh_ngoc, dialog));
        helpView.findViewById(R.id.ll_expert_donald_trump).setOnClickListener(v -> showCallResult("Donald Trump", R.drawable.donald_trump, dialog));
        helpView.findViewById(R.id.ll_expert_bill_gate).setOnClickListener(v -> showCallResult("Bill Gate", R.drawable.bill_gate, dialog));

        dialog.show();
    }

    private void showCallResult(String name, int avatarRes, AlertDialog parentDialog) {
        parentDialog.dismiss();
        gameManager.usePhone();
        btnHelpPhone.setAlpha(0.4f);
        btnHelpPhone.setEnabled(false);

        View resView = LayoutInflater.from(this).inflate(R.layout.dialog_call_result, null);
        AlertDialog resDialog = new AlertDialog.Builder(this).setView(resView).create();

        ImageView avatarView = resView.findViewById(R.id.iv_expert_avatar);
        avatarView.setImageResource(avatarRes);
        ((TextView) resView.findViewById(R.id.tv_expert_name)).setText(name);

        int correctIndex = gameManager.getCorrectIndex();
        int currentLevel = gameManager.getCurrentIndex();

        int accuracy;
        if (currentLevel < 5) {
            accuracy = 100;
        } else if (currentLevel < 10) {
            accuracy = 80;
        } else {
            accuracy = 60;
        }

        String suggested;
        if (new Random().nextInt(100) < accuracy) {
            suggested = OPTION_LABELS[correctIndex].substring(0, 1);
        } else {
            int wrong = (correctIndex + 1) % 4;
            suggested = OPTION_LABELS[wrong].substring(0, 1);
        }

        ((TextView) resView.findViewById(R.id.tv_expert_answer)).setText("Theo tôi đáp án đúng là " + suggested);
        resView.findViewById(R.id.btn_close_call).setOnClickListener(v -> resDialog.dismiss());

        resDialog.show();
    }

    private int dp(int dp) { return Math.round(dp * getResources().getDisplayMetrics().density); }
    public static String formatMoney(long amount) { return String.format("%,d", amount).replace(',', '.') + " đ"; }
}