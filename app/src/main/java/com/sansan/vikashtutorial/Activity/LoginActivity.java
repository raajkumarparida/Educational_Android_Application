package com.sansan.vikashtutorial.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sansan.vikashtutorial.Home.HomeActivity;
import com.sansan.vikashtutorial.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private SignInButton loginBtn;
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing in...");
        progressDialog.setCancelable(false);
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setSize(SignInButton.SIZE_WIDE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        checkIfUserIsSignedIn();

        loginBtn.setOnClickListener(v -> signIn());

        setupPrivacyPolicyText();
    }

    private void checkIfUserIsSignedIn() {
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (isLoggedIn && currentUser != null) {
            navigateToMainActivity();
        }
    }

    private void signIn() {
        showProgressBar();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed.", Toast.LENGTH_SHORT).show();
                hideProgressBar();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    hideProgressBar();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(KEY_IS_LOGGED_IN, true);
                            editor.apply();

                            checkAndSaveUserData(user);
                        }
                    } else {
                        Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkAndSaveUserData(FirebaseUser user) {
        if (user != null) {
            showProgressBar();
            String uid = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            navigateToMainActivity();
                        } else {
                            saveUserDataToFirestore(user);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.w("LoginActivity", "Error checking user data", e);
                        Toast.makeText(this, "Error checking user data. Please try again.", Toast.LENGTH_SHORT).show();
                        hideProgressBar();
                    });
        }
    }

    private void saveUserDataToFirestore(FirebaseUser user) {
        if (user != null) {
            showProgressBar();
            String uid = user.getUid();
            String name = user.getDisplayName() != null ? user.getDisplayName() : "Unknown Name";
            String email = user.getEmail() != null ? user.getEmail() : "Unknown Email";
            String photoUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;
            String phone = "";
            String pin = "";
            String landmark = "";
            String city = "";
            String state = "";
            String district = "";
            String loginSince = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            Boolean isClass11th = true;
            Boolean isClass12th = false;
            String selectedClass = null;

            LoginActivity.User userData = new LoginActivity.User(
                    uid, name, email, photoUrl, phone, pin, landmark, city, district, state, loginSince, isClass11th, isClass12th, selectedClass
            );

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(uid)
                    .set(userData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("LoginActivity", "User data saved successfully");
                        navigateToMainActivity();
                    })
                    .addOnFailureListener(e -> {
                        Log.w("LoginActivity", "Error saving user data", e);
                        Toast.makeText(this, "Error saving user data. Please try again.", Toast.LENGTH_SHORT).show();
                        hideProgressBar();
                    });
        }
    }



    private void navigateToMainActivity() {
        hideProgressBar();
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void showProgressBar() {
        progressDialog.show();
    }

    private void hideProgressBar() {
        progressDialog.dismiss();
    }


    private void setupPrivacyPolicyText() {
        TextView ppText = findViewById(R.id.privacyPolicyText);
        String text = "By continuing,\nI reed this Privacy Policy";
        SpannableString spannableString = new SpannableString(text);

        int customColor = ContextCompat.getColor(this, R.color.red);

        int start1 = text.indexOf("Privacy Policy");
        int end1 = start1 + "Privacy Policy".length();
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                DocumentReference docRef = firestore.collection("appInfo").document("privacyPolicy");

                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        String privacyPolicyUrl = documentSnapshot.getString("url");

                        if (privacyPolicyUrl != null && !privacyPolicyUrl.isEmpty()) {

                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl));
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Privacy policy URL not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle possible errors
                        Toast.makeText(LoginActivity.this, "Failed to load privacy policy.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(customColor);
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(new ForegroundColorSpan(customColor), start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan1, start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ppText.setText(spannableString);
        ppText.setMovementMethod(LinkMovementMethod.getInstance());
        ppText.setHighlightColor(Color.TRANSPARENT);
    }

    public static class User {
        private String uid;
        private String name;
        private String email;
        private String photoUrl;
        private String phone;
        private String pin;
        private String landmark;
        private String city;
        private String district;
        private String state;
        private String loginSince;
        private Boolean class11th;
        private Boolean class12th;
        private String selectedClass;


        public User() {
        }

        public User(String uid, String name, String email, String photoUrl, String phone, String pin, String landmark, String city, String district, String state, String loginSince, Boolean class11th, Boolean class12th, String selectedClass) {
            this.uid = uid;
            this.name = name;
            this.email = email;
            this.photoUrl = photoUrl;
            this.phone = phone;
            this.pin = pin;
            this.landmark = landmark;
            this.city = city;
            this.district = district;
            this.state = state;
            this.loginSince = loginSince;
            this.class11th = class11th;
            this.class12th = class12th;
            this.selectedClass = selectedClass;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPin() {
            return pin;
        }

        public void setPin(String pin) {
            this.pin = pin;
        }

        public String getLandmark() {
            return landmark;
        }

        public void setLandmark(String landmark) {
            this.landmark = landmark;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getLoginSince() {
            return loginSince;
        }

        public void setLoginSince(String loginSince) {
            this.loginSince = loginSince;
        }

        public Boolean getClass11th() {
            return class11th;
        }

        public void setClass11th(Boolean class11th) {
            this.class11th = class11th;
        }

        public Boolean getClass12th() {
            return class12th;
        }

        public void setClass12th(Boolean class12th) {
            this.class12th = class12th;
        }

        public String getSelectedClass() {
            return selectedClass;
        }

        public void setSelectedClass(String selectedClass) {
            this.selectedClass = selectedClass;
        }
    }
}