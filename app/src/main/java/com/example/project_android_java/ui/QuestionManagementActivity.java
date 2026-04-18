package com.example.project_android_java.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_android_java.R;
import com.example.project_android_java.database.DatabaseHelper;
import com.example.project_android_java.manager.QuestionManager;

import java.util.ArrayList;
import java.util.List;

public class QuestionManagementActivity extends AppCompatActivity {

    private ListView lvQuestions;
    private TextView tvEmpty, tvQuestionCount;
    private Button btnAddQuestion, btnBack, btnClearFilter;
    private EditText etSearch;
    private Spinner spFilterLevel;
    private DatabaseHelper dbHelper;
    private List<String[]> questions, filteredQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_management);

        dbHelper = new DatabaseHelper(this);
        initViews();
        loadQuestions();
    }

    private void initViews() {
        lvQuestions = findViewById(R.id.lv_questions);
        tvEmpty = findViewById(R.id.tv_empty);
        tvQuestionCount = findViewById(R.id.tv_question_count);
        btnAddQuestion = findViewById(R.id.btn_add_question);
        btnBack = findViewById(R.id.btn_back);
        btnClearFilter = findViewById(R.id.btn_clear_filter);
        etSearch = findViewById(R.id.et_search);
        spFilterLevel = findViewById(R.id.sp_filter_level);

        String[] levels = new String[16];
        levels[0] = "Tất cả";
        for (int i = 1; i < 16; i++) levels[i] = "Cấp " + i;
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_dropdown_item, levels);
        levelAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spFilterLevel.setAdapter(levelAdapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        spFilterLevel.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        btnClearFilter.setOnClickListener(v -> {
            etSearch.setText("");
            spFilterLevel.setSelection(0);
        });

        btnAddQuestion.setOnClickListener(v -> showEditDialog(-1, null));
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadQuestions() {
        questions = dbHelper.getAllQuestions();
        filteredQuestions = new ArrayList<>(questions);
        displayQuestions();
    }

    private void applyFilters() {
        String searchText = etSearch.getText().toString().trim().toLowerCase();
        int selectedLevel = spFilterLevel.getSelectedItemPosition();

        filteredQuestions = new ArrayList<>();
        for (String[] q : questions) {
            boolean matchesSearch = searchText.isEmpty() ||
                    q[1].toLowerCase().contains(searchText);
            boolean matchesLevel = selectedLevel == 0 ||
                    Integer.parseInt(q[7]) == selectedLevel;

            if (matchesSearch && matchesLevel) {
                filteredQuestions.add(q);
            }
        }

        boolean hasFilter = !searchText.isEmpty() || selectedLevel > 0;
        btnClearFilter.setVisibility(hasFilter ? View.VISIBLE : View.GONE);

        displayQuestions();
    }

    private void displayQuestions() {
        if (filteredQuestions.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            lvQuestions.setVisibility(View.GONE);
            tvQuestionCount.setText("Tổng câu hỏi: 0");
        } else {
            tvEmpty.setVisibility(View.GONE);
            lvQuestions.setVisibility(View.VISIBLE);
            tvQuestionCount.setText("Tổng câu hỏi: " + filteredQuestions.size());

            QuestionAdapter adapter = new QuestionAdapter(this, filteredQuestions);
            lvQuestions.setAdapter(adapter);
        }
    }

    private void showEditDialog(int questionId, String[] questionData) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_question);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.btn_rect_purple);

        EditText etQuestion = dialog.findViewById(R.id.et_question);
        EditText etOptionA = dialog.findViewById(R.id.et_option_a);
        EditText etOptionB = dialog.findViewById(R.id.et_option_b);
        EditText etOptionC = dialog.findViewById(R.id.et_option_c);
        EditText etOptionD = dialog.findViewById(R.id.et_option_d);
        Spinner spCorrect = dialog.findViewById(R.id.sp_correct_answer);
        Spinner spLevel = dialog.findViewById(R.id.sp_level);
        EditText etCategory = dialog.findViewById(R.id.et_category);
        EditText etEvidence = dialog.findViewById(R.id.et_evidence);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        ArrayAdapter<String> answerAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_dropdown_item,
                new String[]{"A", "B", "C", "D"});
        answerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spCorrect.setAdapter(answerAdapter);

        String[] levels = new String[15];
        for (int i = 0; i < 15; i++) levels[i] = String.valueOf(i + 1);
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_dropdown_item, levels);
        levelAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spLevel.setAdapter(levelAdapter);

        if (questionData != null) {
            etQuestion.setText(questionData[1]);
            etOptionA.setText(questionData[2]);
            etOptionB.setText(questionData[3]);
            etOptionC.setText(questionData[4]);
            etOptionD.setText(questionData[5]);
            spCorrect.setSelection(Integer.parseInt(questionData[6]));
            spLevel.setSelection(Integer.parseInt(questionData[7]) - 1);
            etCategory.setText(questionData[8]);
            etEvidence.setText(questionData[9]);
        }

        btnSave.setOnClickListener(v -> {
            String question = etQuestion.getText().toString().trim();
            String optionA = etOptionA.getText().toString().trim();
            String optionB = etOptionB.getText().toString().trim();
            String optionC = etOptionC.getText().toString().trim();
            String optionD = etOptionD.getText().toString().trim();
            int correct = spCorrect.getSelectedItemPosition();
            int level = spLevel.getSelectedItemPosition() + 1;
            String category = etCategory.getText().toString().trim();
            String evidence = etEvidence.getText().toString().trim();

            if (question.isEmpty() || optionA.isEmpty() || optionB.isEmpty() ||
                    optionC.isEmpty() || optionD.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (category.isEmpty()) category = "General";

            if (questionId == -1) {
                dbHelper.insertQuestion(question, optionA, optionB, optionC, optionD, correct, level, category, evidence);
                Toast.makeText(this, "Đã thêm câu hỏi mới!", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.updateQuestion(questionId, question, optionA, optionB, optionC, optionD, correct, level, category, evidence);
                Toast.makeText(this, "Đã cập nhật câu hỏi!", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
            loadQuestions();
            QuestionManager.getInstance(this).refreshQuestions();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showDeleteConfirmDialog(int questionId, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa câu hỏi")
                .setMessage("Bạn có chắc muốn xóa câu hỏi này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    dbHelper.deleteQuestion(questionId);
                    Toast.makeText(this, "Đã xóa câu hỏi!", Toast.LENGTH_SHORT).show();
                    loadQuestions();
                    QuestionManager.getInstance(this).refreshQuestions();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    class QuestionAdapter extends ArrayAdapter<String[]> {
        private final List<String[]> data;

        public QuestionAdapter(Context context, List<String[]> data) {
            super(context, R.layout.item_question);
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_question, parent, false);
            }

            String[] q = data.get(position);
            int id = Integer.parseInt(q[0]);

            TextView tvQuestionText = convertView.findViewById(R.id.tv_question_text);
            TextView tvQuestionInfo = convertView.findViewById(R.id.tv_question_info);
            Button btnEdit = convertView.findViewById(R.id.btn_edit);
            Button btnDelete = convertView.findViewById(R.id.btn_delete);

            tvQuestionText.setText(q[1]);

            int correctIndex = Integer.parseInt(q[6]);
            String correctLabel;
            if (correctIndex == 0) {
                correctLabel = "A";
            } else if (correctIndex == 1) {
                correctLabel = "B";
            } else if (correctIndex == 2) {
                correctLabel = "C";
            } else {
                correctLabel = "D";
            }
            tvQuestionInfo.setText("Level: " + q[7] + " | Đáp án đúng: " + correctLabel);

            btnEdit.setOnClickListener(v -> showEditDialog(id, q));
            btnDelete.setOnClickListener(v -> showDeleteConfirmDialog(id, position));

            return convertView;
        }

        @Override
        public String[] getItem(int position) {
            return data.get(position);
        }
    }
}