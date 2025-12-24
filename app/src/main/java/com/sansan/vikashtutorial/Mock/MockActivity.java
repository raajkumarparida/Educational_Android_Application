package com.sansan.vikashtutorial.Mock;

import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sansan.vikashtutorial.Adapter.QuestionSummaryAdapter;
import com.sansan.vikashtutorial.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SHARE_REQUEST_CODE = 1;

    public static List<MockItem.QuestionModel> questionModelList;
    public static String time = "";
    private List<String> userAnswers = new ArrayList<>();

    private long timeLeftInMillis;
    private boolean isTimerRunning = false;

    private TextView questionIndicatorTextView;
    private TextView timerIndicatorTextView;
    private TextView questionTextView;
    private ProgressBar questionProgressIndicator;
    private Button btn0, btn1, btn2, btn3, nextBtn, prevBtn;

    private int currentQuestionIndex = 0;
    private String selectedAnswer = "";
    private int score = 0;
    private int wrongAnswer = 0;
    private int skippedQuestion = 0;
    private int notAttempted = 0;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private long startTime, endTime;

    private ImageButton bookmark, share, pause, showQuestions;

    private CountDownTimer countDownTimer;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        startTime = System.currentTimeMillis();

        String appName = getString(R.string.app_name);
        String packageName = this.getApplicationContext().getPackageName();
        String appLink = "https://play.google.com/store/apps/details?id=" + packageName;

        MockItem quizModel = getIntent().getParcelableExtra("quizModel");
        if (quizModel != null) {
            questionModelList = quizModel.getQuestionList();
            time = quizModel.getTime();
        } else {
            Toast.makeText(this, "Quiz data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        showQuestions = findViewById(R.id.viewQuestion);
        showQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showQuestionSummary();
            }
        });
        questionIndicatorTextView = findViewById(R.id.question_indicator_textview);
        timerIndicatorTextView = findViewById(R.id.timer_indicator_textview);
        questionTextView = findViewById(R.id.question_textview);
        questionProgressIndicator = findViewById(R.id.question_progress_indicator);
        bookmark = findViewById(R.id.bookmarkBtn);
        share = findViewById(R.id.shareBtn);
        pause = findViewById(R.id.pauseBtn);

        btn0 = findViewById(R.id.btn0);
        btn0.setCompoundDrawablesWithIntrinsicBounds(R.drawable.opa, 0, 0, 0);
        btn1 = findViewById(R.id.btn1);
        btn1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.opb, 0, 0, 0);
        btn2 = findViewById(R.id.btn2);
        btn2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.opc, 0, 0, 0);
        btn3 = findViewById(R.id.btn3);
        btn3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.opd, 0, 0, 0);
        nextBtn = findViewById(R.id.next_btn);
        nextBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.next, 0);
//        prevBtn = findViewById(R.id.prev_btn);
//        prevBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.prev, 0, 0, 0);

        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
//        prevBtn.setOnClickListener(this);

        initializeUserAnswers();
        loadQuestions();
        startTimer(Long.parseLong(time) * 60 * 1000L);


        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseTimer();
                pause.setImageResource(R.drawable.resume);
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MockActivity.this);
                View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetView.findViewById(R.id.resumeButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pause.setImageResource(R.drawable.pause);
                        resumeTimer();
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetView.findViewById(R.id.exitButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

                bottomSheetDialog.setCancelable(false);

                bottomSheetDialog.show();
            }
        });


        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                String currentQuestion = getCurrentQuestion();
                String correctAnswer = getCorrectAnswer();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference bookmarksRef = db.collection("users").document(userId).collection("QuizBookmarked");



                bookmarksRef.whereEqualTo("question", currentQuestion)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    // Bookmark exists, so remove it
                                    for (DocumentSnapshot document : task.getResult()) {
                                        bookmarksRef.document(document.getId()).delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Snackbar.make(findViewById(android.R.id.content), "Bookmark Removed!", Snackbar.LENGTH_SHORT).show();

                                                        bookmark.setImageResource(R.drawable.unfavorait);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Snackbar.make(findViewById(android.R.id.content), "Error removing Bookmark!", Snackbar.LENGTH_SHORT).show();

                                                    }
                                                });
                                    }
                                } else {
                                    Map<String, Object> bookmarkData = new HashMap<>();
                                    bookmarkData.put("question", currentQuestion);
                                    bookmarkData.put("correctAnswer", correctAnswer);
                                    bookmarkData.put("timestamp", FieldValue.serverTimestamp());

                                    bookmarksRef.add(bookmarkData)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Snackbar.make(findViewById(android.R.id.content), "Bookmark Added!", Snackbar.LENGTH_SHORT).show();

                                                    bookmark.setImageResource(R.drawable.favorait);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Snackbar.make(findViewById(android.R.id.content), "Error Adding Bookmark!", Snackbar.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        });
            }

            private String getCurrentQuestion() {
                return questionModelList.get(currentQuestionIndex).getQuestion();
            }

            private String getCorrectAnswer() {
                return questionModelList.get(currentQuestionIndex).getCorrect();
            }
        });

        share.setOnClickListener(view -> {
            pauseTimer();
            MockItem.QuestionModel currentQuestion = questionModelList.get(currentQuestionIndex);
            String questionText = currentQuestion.getQuestion();
            List<String> options = currentQuestion.getOptions();

            String shareBody = "*Question:* " + questionText + "\n\n*Options:*\n";
            for (int i = 0; i < options.size(); i++) {
                shareBody += (i + 1) + ". " + options.get(i) + "\n";
            }

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Quiz Question");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody + "\nCheck out this " + appName + " app on Google Play Store: " + appLink);
            startActivityForResult(Intent.createChooser(sharingIntent, "Share via"), SHARE_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHARE_REQUEST_CODE) {
            resumeTimer();
        }
    }

    private void startTimer(long totalTimeInMillis) {
        timeLeftInMillis = totalTimeInMillis;
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long remainingSeconds = seconds % 60;
                timerIndicatorTextView.setText(String.format("%02d:%02d", minutes, remainingSeconds));
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;

                for (MockItem.QuestionModel question : questionModelList) {
                    if (question.getUserAnswer() == null || question.getUserAnswer().isEmpty()) {
                        notAttempted++;
                    }}
                finishQuiz();
            }
        }.start();
        isTimerRunning = true;
    }

    private void pauseTimer() {
        if (countDownTimer != null && isTimerRunning) {
            countDownTimer.cancel();
            isTimerRunning = false;
        }
    }


    private void resumeTimer() {
        if (!isTimerRunning) {
            startTimer(timeLeftInMillis);
        }
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void initializeUserAnswers() {
        userAnswers = new ArrayList<>();
        for (int i = 0; i < questionModelList.size(); i++) {
            userAnswers.add("N/A");
        }
    }

    private void loadQuestions() {
        selectedAnswer = "";
        if (currentQuestionIndex == questionModelList.size()) {
            finishQuiz();
            return;
        }

        questionIndicatorTextView.setText("Question " + (currentQuestionIndex + 1) + "/" + questionModelList.size());
        questionProgressIndicator.setProgress((int) ((currentQuestionIndex / (float) questionModelList.size()) * 100));
        questionTextView.setText(questionModelList.get(currentQuestionIndex).getQuestion());
        btn0.setText(questionModelList.get(currentQuestionIndex).getOptions().get(0));
        btn1.setText(questionModelList.get(currentQuestionIndex).getOptions().get(1));
        btn2.setText(questionModelList.get(currentQuestionIndex).getOptions().get(2));
        btn3.setText(questionModelList.get(currentQuestionIndex).getOptions().get(3));


//        if (currentQuestionIndex == 0) {
//            prevBtn.setVisibility(View.INVISIBLE);
//        } else {
//            prevBtn.setVisibility(View.VISIBLE);
//        }

        checkIfCurrentQuestionIsBookmarked();
    }

    private void showQuestionSummary() {
        pauseTimer();
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_question_summary, null);

        RecyclerView questionGridRecyclerView = bottomSheetView.findViewById(R.id.questionGridRecyclerView);
        questionGridRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        QuestionSummaryAdapter adapter = new QuestionSummaryAdapter(
                this,
                userAnswers,
                questionModelList.size()
        );
        questionGridRecyclerView.setAdapter(adapter);

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setOnDismissListener(dialog -> {
            resumeTimer();
        });

        bottomSheetDialog.show();
    }


    private void checkIfCurrentQuestionIsBookmarked() {
        progressDialog.show();
        pauseTimer();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String currentQuestion = questionModelList.get(currentQuestionIndex).getQuestion();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference bookmarksRef = db.collection("users").document(userId).collection("QuizBookmarked");

        bookmarksRef.whereEqualTo("question", currentQuestion)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        resumeTimer();
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            questionModelList.get(currentQuestionIndex).setBookmarked(true);
                            bookmark.setImageResource(R.drawable.favorait);
                        } else {
                            questionModelList.get(currentQuestionIndex).setBookmarked(false);
                            bookmark.setImageResource(R.drawable.unfavorait);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        resumeTimer();
                    }
                });
    }

    @Override
    public void onClick(View view) {
        btn0.setBackgroundColor(getResources().getColor(R.color.grey));
        btn1.setBackgroundColor(getResources().getColor(R.color.grey));
        btn2.setBackgroundColor(getResources().getColor(R.color.grey));
        btn3.setBackgroundColor(getResources().getColor(R.color.grey));

        Button clickedBtn = (Button) view;

        if (clickedBtn.getId() == R.id.next_btn) {
            if (!selectedAnswer.isEmpty()) {
                if (currentQuestionIndex < userAnswers.size()) {
                    userAnswers.set(currentQuestionIndex, selectedAnswer);
                } else {
                    userAnswers.add(currentQuestionIndex, selectedAnswer);
                }

                // Check if the answer is correct
                if (selectedAnswer.equals(questionModelList.get(currentQuestionIndex).getCorrect())) {
                    score++;
                } else {
                    wrongAnswer++;
                }
            } else {
                // Handle skipped question
                if (currentQuestionIndex < userAnswers.size()) {
                    userAnswers.set(currentQuestionIndex, "Skipped");
                } else {
                    userAnswers.add("Skipped");
                }
                skippedQuestion++;
                Snackbar.make(findViewById(android.R.id.content), "You Skipped this Question!", Snackbar.LENGTH_SHORT).show();
            }

            // Move to the next question
            currentQuestionIndex++;
            if (currentQuestionIndex < questionModelList.size()) {
                loadQuestions();
            } else {
                finishQuiz();
            }
        } else {
            // Handle answer selection
            selectedAnswer = clickedBtn.getText().toString();
            clickedBtn.setBackgroundColor(getResources().getColor(R.color.selected_answer));
        }

//    } else if (clickedBtn.getId() == R.id.prev_btn) {
//            if (currentQuestionIndex > 0) {
//                currentQuestionIndex--;
//                loadQuestions();
//            } else {
//                Snackbar.make(findViewById(android.R.id.content), "You are already on the first Question!", Snackbar.LENGTH_SHORT).show();
//            }
//        } else {
//            selectedAnswer = clickedBtn.getText().toString();
//            clickedBtn.setBackgroundColor(getResources().getColor(R.color.selected_answer));
//        }
//    }

    }

    private void finishQuiz() {
        if (isFinishing() || isDestroyed()) return;

        int totalQuestions = questionModelList.size();
        int correct = score;
        int skipped = skippedQuestion;
        int wrong = wrongAnswer;
        int notAttempted = totalQuestions - (correct + wrong + skipped);
        int Attempted = totalQuestions - notAttempted;

        String quizTitle = getIntent().getStringExtra("quizTitle");
        String quizSubject = getIntent().getStringExtra("quizSubject");
        MockItem quizModel = getIntent().getParcelableExtra("quizModel");


        long totalTime = Long.parseLong(time) * 60 * 1000L;
        long timeTaken = totalTime - timeLeftInMillis;
        float accuracy = totalQuestions > 0 ? (correct / (float) totalQuestions) * 100 : 0f;

        Bundle bundle = new Bundle();
        bundle.putParcelable("quizModel", quizModel);
        bundle.putString("quizTitle", quizTitle);
        bundle.putString("quizSubject", quizSubject);
        bundle.putInt("totalQuestions", totalQuestions);
        bundle.putInt("correctAnswers", correct);
        bundle.putInt("wrongAnswers", wrong);
        bundle.putInt("skippedAnswers", skipped);
        bundle.putFloat("accuracy", accuracy);
        bundle.putLong("timeTaken", timeTaken);
        bundle.putInt("notAttempted", notAttempted);
        bundle.putInt("Attempted", Attempted);
        bundle.putStringArrayList("userAnswers", new ArrayList<>(userAnswers));

        CustomResultBottomSheet bottomSheet = new CustomResultBottomSheet();
        bottomSheet.setArguments(bundle);
        bottomSheet.setCancelable(false);
        bottomSheet.show(getSupportFragmentManager(), "ResultBottomSheet");
        stopTimer();
    }

    @Override
    protected void onDestroy() {

        endTime = System.currentTimeMillis();
        long timeSpent = endTime - startTime;

        updateTimeInFirestore(timeSpent);

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        super.onDestroy();
    }

    private void updateTimeInFirestore(long timeSpent) {
        String userId = mAuth.getCurrentUser().getUid();
        String quizId = getIntent().getStringExtra("quizTitle");

        if (userId != null) {
            // Check if the document exists
            db.collection("users").document(userId)
                    .collection("mockSpendTime")
                    .document(quizId)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            long previousTime = snapshot.getLong("timeSpent") != null ? snapshot.getLong("timeSpent") : 0;
                            long updatedTime = previousTime + timeSpent;

                            db.collection("users").document(userId)
                                    .collection("mockSpendTime")
                                    .document(quizId)
                                    .update("timeSpent", updatedTime)
                                    .addOnSuccessListener(aVoid -> {

                                    })
                                    .addOnFailureListener(e -> {

                                    });
                        } else {
                            Map<String, Object> timeData = new HashMap<>();
                            timeData.put("timeSpent", timeSpent);

                            db.collection("users").document(userId)
                                    .collection("mockSpendTime")
                                    .document(quizId)
                                    .set(timeData)
                                    .addOnSuccessListener(aVoid -> {

                                    })
                                    .addOnFailureListener(e -> {

                                    });
                        }
                    })
                    .addOnFailureListener(e -> {

                    });
        }
    }

}
