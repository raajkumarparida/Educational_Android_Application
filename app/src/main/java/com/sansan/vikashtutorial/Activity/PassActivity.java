package com.sansan.vikashtutorial.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sansan.vikashtutorial.R;

public class PassActivity extends AppCompatActivity {

//    private FirebaseFirestore db;
//    private FirebaseAuth auth;
//    private EditText inputOpt1, inputOpt2, inputOpt3, inputOpt4, inputOpt5, inputOpt6;
//    private Button submitButton;
//    private static final int MAX_ATTEMPTS = 3;
//    private ProgressDialog progressDialog;
//    private CardView codeArea;
//    private ImageView statusImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);

//        db = FirebaseFirestore.getInstance();
//        auth = FirebaseAuth.getInstance();
//
//        inputOpt1 = findViewById(R.id.inputopt1);
//        inputOpt2 = findViewById(R.id.inputopt2);
//        inputOpt3 = findViewById(R.id.inputopt3);
//        inputOpt4 = findViewById(R.id.inputopt4);
//        inputOpt5 = findViewById(R.id.inputopt5);
//        inputOpt6 = findViewById(R.id.inputopt6);
//        submitButton = findViewById(R.id.submitButton);
//        codeArea = findViewById(R.id.codeArea);
//        statusImageView = findViewById(R.id.statusImageView);
//        TextView toolBarText = findViewById(R.id.toolbarText);
//        toolBarText.setSelected(true);
//
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
//
//        submitButton.setOnClickListener(v -> validateCode());
//
//
//        checkUserStatus();
//        setUpOtpInputNavigation();
//    }
//
//    private void setUpOtpInputNavigation() {
//        EditText[] otpFields = {inputOpt1, inputOpt2, inputOpt3, inputOpt4, inputOpt5, inputOpt6};
//
//        for (int i = 0; i < otpFields.length; i++) {
//            final int index = i;
//
//            otpFields[index].addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {}
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    if (s.length() == 1) {
//                        if (index < otpFields.length - 1) {
//                            if (index == 0 || !otpFields[index - 1].getText().toString().isEmpty()) {
//                                otpFields[index + 1].requestFocus();
//                            }
//                        }
//                    } else if (s.length() == 0 && index > 0) {
//                        otpFields[index - 1].requestFocus();
//                    }
//                }
//            });
//
//            otpFields[index].setOnKeyListener((v, keyCode, event) -> {
//                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && otpFields[index].getText().toString().isEmpty()) {
//                    if (index > 0) {
//                        otpFields[index - 1].requestFocus();
//                    }
//                }
//                return false;
//            });
//        }
//    }
//
//
//    private void validateCode() {
//
//        String enteredCode = inputOpt1.getText().toString().trim() +
//                inputOpt2.getText().toString().trim() +
//                inputOpt3.getText().toString().trim() +
//                inputOpt4.getText().toString().trim() +
//                inputOpt5.getText().toString().trim() +
//                inputOpt6.getText().toString().trim();
//
//        if (enteredCode.isEmpty() || enteredCode.length() != 6) {
//            Toast.makeText(this, "Please enter a valid 6-digit code.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        progressDialog = ProgressDialog.show(this, "Validating Code", "Please wait...", true);
//
//        checkCodeInFirestore(enteredCode);
//    }
//
//    private void checkCodeInFirestore(String enteredCode) {
//        db.collection("Codes").whereEqualTo("code", enteredCode)
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    progressDialog.dismiss();
//                    if (!queryDocumentSnapshots.isEmpty()) {
//                        updateUserPremiumStatus();
//                        updateUserAttemptStatus();
//                        String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
//                        deleteCodeFromFirestore(documentId);
//                    } else {
//                        FirebaseUser currentUser = auth.getCurrentUser();
//                        if (currentUser == null) {
//                            Toast.makeText(PassActivity.this, "User not logged in.", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        String userId = currentUser.getUid();
//                        db.collection("users").document(userId)
//                                .get()
//                                .addOnSuccessListener(documentSnapshot -> {
//                                    if (documentSnapshot.exists()) {
//                                        Long currentAttempts = documentSnapshot.getLong("attempts");
//                                        if (currentAttempts == null) {
//                                            currentAttempts = 0L;
//                                        }
//
//                                        long updatedAttempts = currentAttempts + 1;
//
//                                        db.collection("users").document(userId)
//                                                .update("attempts", updatedAttempts)
//                                                .addOnSuccessListener(aVoid -> {
//                                                    if (updatedAttempts >= MAX_ATTEMPTS) {
//                                                        updateUserBlockedStatus();
//                                                        showBlockedStatus();
//                                                    } else {
//                                                        Toast.makeText(PassActivity.this, "Incorrect code. You have " + (MAX_ATTEMPTS - updatedAttempts) + " attempts left.", Toast.LENGTH_SHORT).show();
//                                                    }
//                                                })
//                                                .addOnFailureListener(e -> {
//                                                    Toast.makeText(PassActivity.this, "Failed to update attempts. Please try again.", Toast.LENGTH_SHORT).show();
//                                                });
//                                    }
//                                })
//                                .addOnFailureListener(e -> {
//                                    Toast.makeText(PassActivity.this, "Error fetching user data. Please try again.", Toast.LENGTH_SHORT).show();
//                                });
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    progressDialog.dismiss();
//                    Toast.makeText(this, "Error validating code. Please try again.", Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    private void deleteCodeFromFirestore(String documentId) {
//        db.collection("Codes").document(documentId)
//                .delete()
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(this, "Code deleted successfully.", Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Error deleting code. Please try again.", Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    private void updateUserPremiumStatus() {
//        FirebaseUser currentUser = auth.getCurrentUser();
//        if (currentUser == null) {
//            Toast.makeText(this, "User not logged in. Please log in first.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String userId = currentUser.getUid();
//        db.collection("users").document(userId)
//                .update("premium", true)
//                .addOnSuccessListener(aVoid -> {
//                    showVerifiedStatus();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Failed to activate premium. Please try again.", Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    private void updateUserBlockedStatus() {
//        FirebaseUser currentUser = auth.getCurrentUser();
//        if (currentUser == null) {
//            Toast.makeText(this, "User not logged in. Please log in first.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String userId = currentUser.getUid();
//        db.collection("users").document(userId)
//                .update("blocked", true)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(this, "You have been blocked due to too many incorrect attempts.", Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Failed to update block status. Please try again.", Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    private void updateUserAttemptStatus() {
//        FirebaseUser currentUser = auth.getCurrentUser();
//        if (currentUser == null) {
//            Toast.makeText(this, "User not logged in. Please log in first.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String userId = currentUser.getUid();
//        db.collection("users").document(userId)
//                .get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//
//                        Long currentAttempts = documentSnapshot.getLong("attempts");
//                        if (currentAttempts == null) {
//                            currentAttempts = 0L;
//                        }
//
//                        long updatedAttempts = currentAttempts + 1;
//
//                        if (updatedAttempts >= MAX_ATTEMPTS) {
//                            updateUserBlockedStatus();
//                        } else {
//                            db.collection("users").document(userId)
//                                    .update("attempts", updatedAttempts)
//                                    .addOnSuccessListener(aVoid -> {
//                                        Toast.makeText(PassActivity.this, "Attempt " + updatedAttempts + " of " + MAX_ATTEMPTS, Toast.LENGTH_SHORT).show();
//                                    })
//                                    .addOnFailureListener(e -> {
//                                        Toast.makeText(PassActivity.this, "Failed to update attempts. Please try again.", Toast.LENGTH_SHORT).show();
//                                    });
//                        }
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Error fetching user data. Please try again.", Toast.LENGTH_SHORT).show();
//                });
//    }
//
//
//    private void showBlockedStatus() {
//        codeArea.setVisibility(View.GONE);
//        statusImageView.setImageResource(R.drawable.denied);
//        statusImageView.setVisibility(View.VISIBLE);
//        Toast.makeText(this, "You have been blocked due to too many incorrect attempts.", Toast.LENGTH_LONG).show();
//    }
//
//    private void showVerifiedStatus() {
//        codeArea.setVisibility(View.GONE);
//        statusImageView.setImageResource(R.drawable.accessed);
//        statusImageView.setVisibility(View.VISIBLE);
//        Toast.makeText(this, "Premium activated successfully!", Toast.LENGTH_LONG).show();
//    }
//
//    private void checkUserStatus() {
//        progressDialog = ProgressDialog.show(this, "Checking Status", "Please wait...", true);
//
//        FirebaseUser currentUser = auth.getCurrentUser();
//        if (currentUser == null) {
//            progressDialog.dismiss();
//            return;
//        }
//
//        String userId = currentUser.getUid();
//        db.collection("users").document(userId)
//                .get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    progressDialog.dismiss();
//                    if (documentSnapshot.exists()) {
//                        Boolean isBlocked = documentSnapshot.getBoolean("blocked");
//                        if (Boolean.TRUE.equals(isBlocked)) {
//                            showBlockedStatus();
//                        } else {
//                            checkUserPremiumStatus();
//                        }
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    progressDialog.dismiss();
//                    Toast.makeText(this, "Error checking user status. Please try again.", Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    private void checkUserPremiumStatus() {
//        FirebaseUser currentUser = auth.getCurrentUser();
//        if (currentUser == null) return;
//
//        String userId = currentUser.getUid();
//        db.collection("users").document(userId)
//                .get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists() && Boolean.TRUE.equals(documentSnapshot.getBoolean("premium"))) {
//                        showVerifiedStatus();
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Error checking premium status. Please try again.", Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            onBackPressed();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
    }
}