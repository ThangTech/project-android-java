package com.example.project_android_java.ui;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.project_android_java.R;
import java.util.Random;

public class HelpAudienceActivity extends AppCompatActivity {

    private View[] viewBars;
    private TextView[] tvPercents;
    private Button btnCloseAudience;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_audience_help);

        int correctIndex = getIntent().getIntExtra("CORRECT_INDEX", 0);
        int questionIndex = getIntent().getIntExtra("QUESTION_INDEX", 0);

        initViews();
        startAudienceProcess(correctIndex, questionIndex);
    }

    private void initViews() {
        findViewById(R.id.layout_audience_chart).setVisibility(View.VISIBLE);
        viewBars = new View[]{
                findViewById(R.id.view_bar_a),
                findViewById(R.id.view_bar_b),
                findViewById(R.id.view_bar_c),
                findViewById(R.id.view_bar_d)
        };
        tvPercents = new TextView[]{
                findViewById(R.id.tv_percent_a),
                findViewById(R.id.tv_percent_b),
                findViewById(R.id.tv_percent_c),
                findViewById(R.id.tv_percent_d)
        };
        btnCloseAudience = findViewById(R.id.btn_close_audience);
        btnCloseAudience.setVisibility(View.INVISIBLE);
        btnCloseAudience.setOnClickListener(v -> finish());

        for (int i = 0; i < 4; i++) {
            ViewGroup.LayoutParams lp = viewBars[i].getLayoutParams();
            lp.height = 0;
            viewBars[i].setLayoutParams(lp);
            tvPercents[i].setText("0%");
        }
    }

    private void startAudienceProcess(int correctIndex, int questionIndex) {
        // Wait 5 seconds as requested
        handler.postDelayed(() -> showAudienceResultAnimated(correctIndex, questionIndex), 5000);
    }

    private void showAudienceResultAnimated(int correctIndex, int questionIndex) {
        int[] percents = new int[4];
        Random rnd = new Random();
        
        int accuracy = (questionIndex < 10) ? 75 : 45;
        percents[correctIndex] = accuracy + rnd.nextInt(20);
        
        int remaining = 100 - percents[correctIndex];
        for (int i = 0; i < 3; i++) {
            int idx = (correctIndex + 1 + i) % 4;
            if (i == 2) {
                percents[idx] = remaining;
            } else {
                percents[idx] = rnd.nextInt(remaining);
                remaining -= percents[idx];
            }
        }

        int maxHeight = dp(200); 
        
        for (int i = 0; i < 4; i++) {
            final int index = i;
            final int targetPercent = percents[i];
            final int targetHeight = (targetPercent * maxHeight) / 100;

            ValueAnimator animator = ValueAnimator.ofInt(0, targetHeight);
            animator.setDuration(2000);
            animator.addUpdateListener(animation -> {
                int val = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams lp = viewBars[index].getLayoutParams();
                lp.height = val;
                viewBars[index].setLayoutParams(lp);
                
                int currentPercent = (val * 100) / maxHeight;
                tvPercents[index].setText(currentPercent + "%");
            });
            animator.start();
        }
        
        handler.postDelayed(() -> btnCloseAudience.setVisibility(View.VISIBLE), 2000);
    }

    private int dp(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
