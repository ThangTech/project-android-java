package com.example.project_android_java.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.project_android_java.R;

public class HelpSageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showSageAdvice();
    }

    private void showSageAdvice() {
        String questionText = getIntent().getStringExtra("QUESTION_TEXT");
        String[] options = getIntent().getStringArrayExtra("QUESTION_OPTIONS");
        int correctIndex = getIntent().getIntExtra("CORRECT_INDEX", 0);
        String evidence = getIntent().getStringExtra("EVIDENCE");
        
        View helpView = LayoutInflater.from(this).inflate(R.layout.dialog_sage_help, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(helpView)
                .setCancelable(false)
                .create();

        TextView tvEvidence = helpView.findViewById(R.id.tv_sage_evidence);
        TextView tvAdvice = helpView.findViewById(R.id.tv_sage_advice);
        
        if (evidence == null || evidence.isEmpty()) {
            evidence = "Không có dẫn chứng cụ thể cho câu hỏi này.";
        }
        
        tvEvidence.setText(evidence);
        tvAdvice.setText("Hãy cân nhắc kỹ trước khi chọn đáp án!");
        
        helpView.findViewById(R.id.btn_close_sage).setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();
    }
}
