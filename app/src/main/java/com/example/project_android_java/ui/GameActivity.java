package com.example.project_android_java.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_android_java.R;
import com.example.project_android_java.manager.QuestionManager;
import com.example.project_android_java.model.Question;

import java.util.List;

/**
 * GAME ACTIVITY - Màn hình chơi game chính.
 *
 * 📚 Kiến thức cần biết:
 *  - Intent.putExtra(): truyền dữ liệu sang Activity khác
 *  - Cập nhật UI động: setText(), setEnabled()
 *  - Mảng Button[]: quản lý 4 nút đáp án gọn hơn
 *  - MONEY_LADDER[]: mảng mốc tiền thưởng
 */
public class GameActivity extends AppCompatActivity {

    // ── Mốc tiền thưởng 15 câu ───────────────────────────────────────────────
    // Index 0 = câu 1, index 14 = câu 15
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
    private TextView tvQuestion;       // Hiển thị câu hỏi
    private TextView tvQuestionNumber; // "Câu 1/15"
    private TextView tvCurrentMoney;   // Số tiền hiện tại
    private Button[] answerButtons;    // [btnA, btnB, btnC, btnD]

    // ── Game State ────────────────────────────────────────────────────────────
    private List<Question> questions;  // 15 câu hỏi của ván này
    private int currentIndex = 0;      // Đang ở câu số mấy (0-14)
    private Question currentQuestion;  // Câu hỏi hiện tại

    // ── Prefix đáp án ─────────────────────────────────────────────────────────
    private static final String[] OPTION_LABELS = {"A. ", "B. ", "C. ", "D. "};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initViews();
        loadQuestions();
        showQuestion();
    }

    // ── Khởi tạo ─────────────────────────────────────────────────────────────

    private void initViews() {
        tvQuestion       = findViewById(R.id.tv_question);
        tvQuestionNumber = findViewById(R.id.tv_question_number);
        tvCurrentMoney   = findViewById(R.id.tv_current_money);

        // Gom 4 nút đáp án vào mảng để xử lý gọn hơn
        answerButtons = new Button[]{
                findViewById(R.id.btn_answer_a),
                findViewById(R.id.btn_answer_b),
                findViewById(R.id.btn_answer_c),
                findViewById(R.id.btn_answer_d)
        };

        // Gắn click listener cho từng nút
        for (int i = 0; i < answerButtons.length; i++) {
            final int index = i; // phải là final để dùng trong lambda
            answerButtons[i].setOnClickListener(v -> onAnswerSelected(index));
        }
    }

    private void loadQuestions() {
        QuestionManager qm = QuestionManager.getInstance(this);
        qm.loadQuestions();
        questions = qm.getGameQuestions();
    }

    // ── Hiển thị câu hỏi ─────────────────────────────────────────────────────

    /**
     * Hiển thị câu hỏi hiện tại lên màn hình.
     * Gọi mỗi khi chuyển sang câu mới.
     */
    private void showQuestion() {
        currentQuestion = questions.get(currentIndex);

        // Cập nhật số câu và tiền
        tvQuestionNumber.setText("Câu " + (currentIndex + 1) + " / 15");
        tvCurrentMoney.setText(formatMoney(MONEY_LADDER[currentIndex]));

        // Hiển thị nội dung câu hỏi
        tvQuestion.setText(currentQuestion.getQuestionText());

        // Hiển thị 4 đáp án
        String[] options = currentQuestion.getOptions();
        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i].setText(OPTION_LABELS[i] + options[i]);
            answerButtons[i].setEnabled(true);
            // Reset màu nút về mặc định (xanh dương)
            answerButtons[i].setBackgroundResource(R.drawable.btn_hex_normal);
        }
    }

    // ── Xử lý chọn đáp án ────────────────────────────────────────────────────

    /**
     * Gọi khi người chơi bấm vào một đáp án.
     * @param selectedIndex: 0=A, 1=B, 2=C, 3=D
     */
    private void onAnswerSelected(int selectedIndex) {
        // Disable tất cả nút để không bấm lại được
        setAllButtonsEnabled(false);

        if (currentQuestion.isCorrect(selectedIndex)) {
            onCorrectAnswer(selectedIndex);
        } else {
            onWrongAnswer(selectedIndex);
        }
    }

    /**
     * Xử lý khi trả lời ĐÚNG.
     * Đổi màu nút sang xanh lá → delay 1.5s → chuyển câu tiếp.
     */
    private void onCorrectAnswer(int selectedIndex) {
        // Đổi màu nút đúng sang xanh lá
        answerButtons[selectedIndex].setBackgroundResource(R.drawable.btn_hex_correct);

        // Delay 1.5 giây rồi chuyển câu tiếp
        // Handler.postDelayed: chạy code sau một khoảng thời gian (ms)
        new android.os.Handler().postDelayed(() -> {
            currentIndex++;

            if (currentIndex >= 15) {
                // Hoàn thành tất cả 15 câu → Thắng!
                goToResult(true, MONEY_LADDER[14]);
            } else {
                // Còn câu tiếp → hiển thị câu mới
                showQuestion();
            }
        }, 1500); // 1500ms = 1.5 giây
    }

    /**
     * Xử lý khi trả lời SAI.
     * Đổi màu nút sai sang đỏ + hiện đáp án đúng xanh → delay → kết thúc.
     */
    private void onWrongAnswer(int selectedIndex) {
        // Nút bị chọn → đỏ
        answerButtons[selectedIndex].setBackgroundResource(R.drawable.btn_hex_wrong);

        // Hiện đáp án đúng → xanh lá
        int correctIndex = currentQuestion.getCorrectAnswerIndex();
        answerButtons[correctIndex].setBackgroundResource(R.drawable.btn_hex_correct);

        // Tính tiền nhận được (về mốc an toàn gần nhất)
        long moneyEarned = getSafeMoney();

        // Delay 2 giây rồi chuyển sang màn hình kết quả
        new android.os.Handler().postDelayed(() -> {
            goToResult(false, moneyEarned);
        }, 2000);
    }

    /**
     * Tính số tiền nhận được khi thua.
     * Trả về tiền của mốc an toàn gần nhất đã vượt qua.
     *  - Chưa qua câu 5  → 0đ
     *  - Qua câu 5-9     → 2.000.000đ
     *  - Qua câu 10+     → 22.000.000đ
     */
    private long getSafeMoney() {
        if (currentIndex >= 10) return MONEY_LADDER[9];  // 22 triệu
        if (currentIndex >= 5)  return MONEY_LADDER[4];  //  2 triệu
        return 0L;
    }

    // ── Điều hướng ───────────────────────────────────────────────────────────

    /**
     * Chuyển sang màn hình kết quả.
     * putExtra(): đóng gói dữ liệu vào Intent để truyền sang Activity khác.
     *
     * @param isWin: true = thắng, false = thua
     * @param money: số tiền đạt được
     */
    private void goToResult(boolean isWin, long money) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("IS_WIN", isWin);
        intent.putExtra("MONEY_EARNED", money);
        intent.putExtra("QUESTION_REACHED", currentIndex + 1);
        startActivity(intent);
        finish(); // Đóng GameActivity, không cho back lại
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void setAllButtonsEnabled(boolean enabled) {
        for (Button btn : answerButtons) {
            btn.setEnabled(enabled);
        }
    }

    /**
     * Format số tiền: 150000000 → "150.000.000 đ"
     */
    public static String formatMoney(long amount) {
        // String.format với grouping separator
        return String.format("%,d đ", amount).replace(',', '.');
    }
}