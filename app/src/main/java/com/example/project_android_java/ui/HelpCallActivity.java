package com.example.project_android_java.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.project_android_java.R;
import java.util.Random;

public class HelpCallActivity extends AppCompatActivity {

    private final String[] OPTION_LABELS = {"A", "B", "C", "D"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This activity doesn't need its own content view immediately, 
        // it will show a dialog as soon as it starts.
        showExpertSelectionDialog();
    }

    private void showExpertSelectionDialog() {
        View helpView = LayoutInflater.from(this).inflate(R.layout.dialog_call_help, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(helpView)
                .setCancelable(false)
                .create();

        helpView.findViewById(R.id.ll_expert_cu_trong_xoay).setOnClickListener(v -> showCallResult("Cù Trọng Xoay", R.drawable.cu_trong_xoay, dialog));
        helpView.findViewById(R.id.ll_expert_truong_anh_ngoc).setOnClickListener(v -> showCallResult("Trương Anh Ngọc", R.drawable.truong_anh_ngoc, dialog));
        helpView.findViewById(R.id.ll_expert_donald_trump).setOnClickListener(v -> showCallResult("Donald Trump", R.drawable.donald_trump, dialog));
        helpView.findViewById(R.id.ll_expert_bill_gate).setOnClickListener(v -> showCallResult("Bill Gate", R.drawable.bill_gate, dialog));

        dialog.show();
    }

    private void showCallResult(String name, int avatarRes, AlertDialog parentDialog) {
        parentDialog.dismiss();

        int correctIndex = getIntent().getIntExtra("CORRECT_INDEX", 0);
        int questionIndex = getIntent().getIntExtra("QUESTION_INDEX", 0);

        View resView = LayoutInflater.from(this).inflate(R.layout.dialog_call_result, null);
        AlertDialog resDialog = new AlertDialog.Builder(this)
                .setView(resView)
                .setCancelable(false)
                .create();

        ImageView avatarView = resView.findViewById(R.id.iv_expert_avatar);
        avatarView.setImageResource(avatarRes);
        ((TextView) resView.findViewById(R.id.tv_expert_name)).setText(name);

        int accuracy = (questionIndex < 10) ? 90 : 70;

        String suggested;
        if (new Random().nextInt(100) < accuracy) {
            suggested = OPTION_LABELS[correctIndex];
        } else {
            int wrong = (correctIndex + 1) % 4;
            suggested = OPTION_LABELS[wrong];
        }

        ((TextView) resView.findViewById(R.id.tv_expert_answer)).setText("Theo tôi đáp án đúng là " + suggested);
        resView.findViewById(R.id.btn_close_call).setOnClickListener(v -> {
            resDialog.dismiss();
            finish();
        });

        resDialog.show();
    }
}
