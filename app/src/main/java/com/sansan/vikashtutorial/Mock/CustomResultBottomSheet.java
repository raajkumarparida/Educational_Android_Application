package com.sansan.vikashtutorial.Mock;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sansan.vikashtutorial.Adapter.QuestionAnswerAdapter;
import com.sansan.vikashtutorial.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomResultBottomSheet extends BottomSheetDialogFragment {

    public static List<MockItem.QuestionModel> questionModelList;
    private PieChart pieChart;
    private FirebaseAuth mAuth;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_custom_result_bottom_sheet, container, false);

        mAuth = FirebaseAuth.getInstance();

        // Handle Firebase user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        TextView userName = view.findViewById(R.id.result_title);
        CircleImageView userImg = view.findViewById(R.id.userImg);

        if (currentUser != null) {
            String userId = currentUser.getUid(); // Get the unique user ID
            String userDisplayName = currentUser.getDisplayName();
            Uri userPhotoUrl = currentUser.getPhotoUrl();

            // Set username or default message
            userName.setText((userDisplayName != null ? userDisplayName : "User") + "\nIt's Your Mock Result");

            // Load user photo or default image
            if (userPhotoUrl != null) {
                Glide.with(this)
                        .load(userPhotoUrl)
                        .circleCrop()
                        .into(userImg);
            } else {
                userImg.setImageResource(R.mipmap.ic_launcher);
            }

            // Fetch the selected class from Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String userClass = "Class not selected";
                        if (documentSnapshot.exists()) {
                            userClass = documentSnapshot.getString("selectedClass");
                            if (userClass == null) {
                                userClass = "Class not selected";
                            }
                        } userName.setText((userDisplayName != null ? userDisplayName : "User") +
                                "\nIt's Your Mock Result" +
                                "\nClass: " + userClass);
                    })
                    .addOnFailureListener(e -> {
                        userName.setText((userDisplayName != null ? userDisplayName : "User") +
                                "\nIt's Your Mock Result" +
                                "\nClass: Failed to load");
                    });
        } else {
            userImg.setImageResource(R.mipmap.ic_launcher);
            userName.setText("Guest\nIt's Your Mock Result\nSelected Class: N/A");
                    }

        // Retrieve data from arguments
        Bundle args = getArguments();
        if (args != null) {
            MockItem quizModel = args.getParcelable("quizModel");
            String fullTitle = args.getString("quizTitle");
            String fullSubject = args.getString("quizSubject");
            int notAttempted = args.getInt("notAttempted", 0);
            int Attempted = args.getInt("Attempted", 0);
            int totalQuestions = args.getInt("totalQuestions", 0);
            int correctAnswers = args.getInt("correctAnswers", 0);
            int wrongAnswers = args.getInt("wrongAnswers", 0);
            int skippedAnswers = args.getInt("skippedAnswers", 0);
            float accuracy = args.getFloat("accuracy", 0f);
            long timeTaken = args.getLong("timeTaken", 0);
            ArrayList<String> userAnswers = getArguments().getStringArrayList("userAnswers");

            if (quizModel != null) {
                questionModelList = quizModel.getQuestionList();
                if (!questionModelList.isEmpty()) {
                    RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    QuestionAnswerAdapter adapter = new QuestionAnswerAdapter(questionModelList, userAnswers);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getActivity(), "No questions available", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Quiz data not found", Toast.LENGTH_SHORT).show();
                dismiss();
                return null;
            }


            pieChart = view.findViewById(R.id.result_pie_chart);
            setupPieChart(correctAnswers, wrongAnswers, skippedAnswers, notAttempted);

            TextView fullTitleTextView = view.findViewById(R.id.mock_title);
            fullTitleTextView.setText(fullTitle + "\n" + fullSubject);

            TextView totalQuestion = view.findViewById(R.id.total_Question);
            totalQuestion.setText(String.valueOf("Question\n" + totalQuestions));

            TextView totalCorrect = view.findViewById(R.id.total_Correct);
            totalCorrect.setText(String.valueOf("Correct\n" + correctAnswers));

            TextView totalWrong = view.findViewById(R.id.total_Wrong);
            totalWrong.setText(String.valueOf("Wrong\n" + wrongAnswers));

            TextView totalSkipped = view.findViewById(R.id.total_Skipped);
            totalSkipped.setText(String.valueOf("Skip\n" + skippedAnswers));

            TextView totalNotAttempted = view.findViewById(R.id.not_Attempted);
            totalNotAttempted.setText(String.valueOf("Not Visit\n" + notAttempted));

            TextView totalAttempted = view.findViewById(R.id.total_Attempted);
            totalAttempted.setText(String.valueOf("Visit\n" + Attempted));

            TextView accuracyTextView = view.findViewById(R.id.result_accuracy);
            accuracyTextView.setText("Accuracy\n" + String.format("%.2f", accuracy) + "%");

            TextView timeTakenTextView = view.findViewById(R.id.result_time_taken);
            timeTakenTextView.setText("Time Taken\n" + (timeTaken / 60000) + " min " + (timeTaken % 60000 / 1000) + " sec");
        }

        view.findViewById(R.id.result_finish_button).setOnClickListener(v -> requireActivity().finish());


        return view;
    }

    private void setupPieChart(int correct, int wrong, int skipped, int notAttempted) {
        List<PieEntry> entries = new ArrayList<>();

        if (correct > 0) {
            entries.add(new PieEntry(correct, "Correct"));
        }
        if (wrong > 0) {
            entries.add(new PieEntry(wrong, "Wrong"));
        }
        if (skipped > 0) {
            entries.add(new PieEntry(skipped, "Skipped"));
        }
        if (notAttempted > 0) {
            entries.add(new PieEntry(notAttempted, "Not Attempted"));
        }

        if (entries.isEmpty()) {
            pieChart.clear();
            pieChart.setNoDataText("No data available");
            pieChart.invalidate();
            return;
        }

        List<Integer> colors = new ArrayList<>();
        for (PieEntry entry : entries) {
            String label = entry.getLabel();
            if ("Correct".equals(label)) {
                colors.add(Color.parseColor("#4CAF50"));
            } else if ("Wrong".equals(label)) {
                colors.add(Color.parseColor("#F44336"));
            } else if ("Skipped".equals(label)) {
                colors.add(Color.parseColor("#81D4FA"));
            } else if ("Not Attempted".equals(label)) {
                colors.add(Color.parseColor("#BDBDBD"));
            }
        }


        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.parseColor("#212121"));
        dataSet.setValueTextSize(12f);
        dataSet.setSliceSpace(3f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new IntegerValueFormatter());

        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        dataSet.setValueTextColor(Color.parseColor("#212121"));
        pieChart.setEntryLabelTextSize(10f);
        dataSet.setValueTextColor(Color.parseColor("#212121"));

        String secureMarkText = correct + "\n" + "━━━" + "\n" + (correct + wrong + skipped + notAttempted);
        pieChart.setCenterText(secureMarkText);
        dataSet.setValueTextColor(Color.parseColor("#212121"));
        pieChart.setCenterTextSize(16f);

        pieChart.setExtraOffsets(10, 10, 10, 10);
        pieChart.invalidate();
    }


    public static class IntegerValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return String.format("%d", (int) value);
        }
    }
}