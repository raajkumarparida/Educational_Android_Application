package com.sansan.vikashtutorial.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.sansan.vikashtutorial.Home.HomeActivity;
import com.sansan.vikashtutorial.R;

public class LauncherActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1000; // 3 seconds
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private View parentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_launcher);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toast.makeText(this, "Launcher Activity", Toast.LENGTH_SHORT).show();

        parentLayout = findViewById(android.R.id.content);

        // Set up the splash screen elements
        ImageView introGif = findViewById(R.id.logoImageView);
        TextView introText = findViewById(R.id.splashTextView);

        // Add text with image
        addTextWithImage(introText);

        // Load the logo with Glide
        Glide.with(this).asBitmap().load(R.mipmap.ic_launcher).into(introGif);

        // Delay and check user login status
        new Handler().postDelayed(this::checkUserLoginStatus, SPLASH_DELAY);
    }

    private void checkUserLoginStatus() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean(KEY_IS_LOGGED_IN, false);

        if (isLoggedIn) {
            Toast.makeText(this, "Already logged in!", Toast.LENGTH_SHORT).show();
            navigateToHomeActivity();
        } else {
            navigateToLoginActivity();
        }
    }

    private void navigateToHomeActivity() {
        Intent intent = new Intent(LauncherActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(LauncherActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void addTextWithImage(TextView textView) {
        String text = "Developed by \nRaj Kumar Parida | ";
        SpannableString spannable = new SpannableString(text + " ");

        // Using compatibility check for drawable resource
        Drawable drawable = getResources().getDrawable(R.drawable.ig, null);
        int drawableSize = (int) 20;
        drawable.setBounds(0, 0, drawableSize, drawableSize);

        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        spannable.setSpan(imageSpan, text.length(), text.length() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        textView.setText(spannable);
    }
}