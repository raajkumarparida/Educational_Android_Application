package com.sansan.vikashtutorial.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.sansan.vikashtutorial.Home.HomeActivity;
import com.sansan.vikashtutorial.R;

public class SplashActivity extends AppCompatActivity {


    private static final int SPLASH_DELAY = 3000; // 2 seconds
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private View parentLayout;

//    private static final int SPLASH_DELAY = 2000; // 2 seconds
//    private static final int NETWORK_CHECK_INTERVAL = 3000; // 3 seconds
//    private static final String PREFS_NAME = "UserPrefs";
//    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
//    private View parentLayout;
//    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

//        View splashBackground = findViewById(R.id.splashBackground);
//        final View logoImageView = findViewById(R.id.logoImageView);
//
//        Animation fadeInBackground = AnimationUtils.loadAnimation(this, R.anim.fade_in_background);
//        splashBackground.startAnimation(fadeInBackground);
//
//        if (isNetworkAvailable()) {
//            fadeInBackground.setAnimationListener(new Animation.AnimationListener() {
//                @Override
//                public void onAnimationStart(Animation animation) {
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation) {
//                    logoImageView.setVisibility(View.VISIBLE);
//                    Animation fadeInLogo = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade_in_logo);
//                    logoImageView.startAnimation(fadeInLogo);
//
//                    fadeInLogo.setAnimationListener(new Animation.AnimationListener() {
//                        @Override
//                        public void onAnimationStart(Animation animation) {
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            checkUserLoginStatus();
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animation animation) {
//                        }
//                    });
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation) {
//                }
//            });
//        } else {
//            showNetworkBottomSheet();
//        }
//    }
//
//    private boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
//        return activeNetwork != null && activeNetwork.isConnected();
//    }
//
//    private void showNetworkBottomSheet() {
//        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
//        View bottomSheetView = getLayoutInflater().inflate(R.layout.network_bottom_sheet, null);
//        bottomSheetDialog.setContentView(bottomSheetView);
//
//        Button retryButton = bottomSheetView.findViewById(R.id.retryButton);
//        Button exitButton = bottomSheetView.findViewById(R.id.exitButton);
//
//        retryButton.setOnClickListener(v -> {
//            if (isNetworkAvailable()) {
//                bottomSheetDialog.dismiss();
//                checkUserLoginStatus();
//            } else {
//                Toast.makeText(this, "No network connection. Please try again.", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        exitButton.setOnClickListener(v -> finish());
//
//        bottomSheetDialog.setCancelable(false);
//        bottomSheetDialog.show();
//    }
//
//    private void checkUserLoginStatus() {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        if (auth.getCurrentUser() != null) {
//            navigateToHomeActivity();
//        } else {
//            navigateToLoginActivity();
//        }
//    }
//
//    private void navigateToHomeActivity() {
//        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
//    private void navigateToLoginActivity() {
//        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//        startActivity(intent);
//        finish();

//        ImageView introGif = findViewById(R.id.logoImageView);
//        TextView introText = findViewById(R.id.splashTextView);
//        addTextWithImage(introText);
//
//        Glide.with(this).asBitmap().load(R.mipmap.ic_launcher).into(introGif);
//
//        new Handler().postDelayed(this::checkNetworkAndProceed, SPLASH_DELAY);
//    }
//
//    private void checkNetworkAndProceed() {
//        if (isNetworkAvailable()) {
//            checkUserLoginStatus();
//        } else {
//            showNetworkBottomSheet();
//        }
//    }
//
//    private boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
//        if (connectivityManager != null) {
//            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
//            if (capabilities != null) {
//                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
//                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
//                    return capabilities.getLinkDownstreamBandwidthKbps() > 100;
//                }
//            }
//        }
//        return false;
//    }
//
//    private void showNetworkBottomSheet() {
//        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
//        View bottomSheetView = getLayoutInflater().inflate(R.layout.network_bottom_sheet, null);
//        bottomSheetDialog.setContentView(bottomSheetView);
//
//        Button retryButton = bottomSheetView.findViewById(R.id.retryButton);
//        Button exitButton = bottomSheetView.findViewById(R.id.exitButton);
//
//        retryButton.setOnClickListener(v -> retryNetworkCheck(bottomSheetDialog));
//        exitButton.setOnClickListener(v -> finish());
//
//        bottomSheetDialog.setCancelable(false);
//        bottomSheetDialog.show();
//    }
//
//    private void retryNetworkCheck(BottomSheetDialog dialog) {
//        new Handler().postDelayed(() -> {
//            if (isNetworkAvailable()) {
//                dialog.dismiss();
//                checkUserLoginStatus();
//            } else {
//                retryNetworkCheck(dialog);
//            }
//        }, NETWORK_CHECK_INTERVAL);
//    }
//
//    private void checkUserLoginStatus() {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        if (auth.getCurrentUser() != null) {
//            navigateToHomeActivity();
//        } else {
//            navigateToLoginActivity();
//        }
//    }
//
//    private void navigateToHomeActivity() {
//        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
//    private void navigateToLoginActivity() {
//        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
//    private void addTextWithImage(TextView textView) {
//        String text = "Developed by \nsansas.io | ";
//        SpannableString spannable = new SpannableString(text + " ");
//        Drawable drawable = getResources().getDrawable(R.drawable.ig);
//        int drawableSize = (int) 20;
//        drawable.setBounds(0, 0, drawableSize, drawableSize);
//        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
//        spannable.setSpan(imageSpan, text.length(), text.length() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        textView.setText(spannable);


        // ding dong
//        parentLayout = findViewById(android.R.id.content);
//
//        ImageView introGif = findViewById(R.id.logoImageView);
//        TextView introText = findViewById(R.id.splashTextView);
//        addTextWithImage(introText);
//
//        Glide.with(this).asBitmap().load(R.mipmap.ic_launcher).into(introGif);
//
//        new Handler().postDelayed(this::checkNetworkAndProceed, SPLASH_DELAY);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        handler.removeCallbacksAndMessages(null);
//    }
//
////    private void checkNetworkAndProceed() {
////        if (isNetworkAvailable()) {
////            double speedMbps = getNetworkSpeedMbps();
////            Snackbar.make(parentLayout, "Connected! Speed: " + speedMbps + " Mbps", Snackbar.LENGTH_LONG).show();
////            checkUserLoginStatus();
////        } else {
////            Snackbar.make(parentLayout, "No Internet. Checking again...", Snackbar.LENGTH_SHORT).show();
////            showNetworkBottomSheet();
////        }
////    }
//
//    private void checkNetworkAndProceed() {
//        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        boolean isLoggedIn = preferences.getBoolean(KEY_IS_LOGGED_IN, false);
//
//        if (isLoggedIn) {
//            Toast.makeText(this, "Already logged in!", Toast.LENGTH_SHORT).show(); // Show toast
//
//            if (isNetworkAvailable()) {
//                double speedMbps = getNetworkSpeedMbps();
//                Snackbar.make(parentLayout, "Connected! Speed: " + speedMbps + " Mbps", Snackbar.LENGTH_LONG).show();
//                navigateToHomeActivity(); // Directly go to HomeActivity if logged in
//            } else {
//                Snackbar.make(parentLayout, "No Internet. Checking again...", Snackbar.LENGTH_SHORT).show();
//                showNetworkBottomSheet();
//            }
//        } else {
//            if (isNetworkAvailable()) {
//                double speedMbps = getNetworkSpeedMbps();
//                Snackbar.make(parentLayout, "Connected! Speed: " + speedMbps + " Mbps", Snackbar.LENGTH_LONG).show();
//                navigateToLoginActivity(); // Navigate to LoginActivity if not logged in
//            } else {
//                Snackbar.make(parentLayout, "No Internet. Checking again...", Snackbar.LENGTH_SHORT).show();
//                showNetworkBottomSheet();
//            }
//        }
//    }
//
//
//    private boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
//        if (connectivityManager != null) {
//            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
//            if (capabilities != null) {
//                return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
//                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
//            }
//        }
//        return false;
//    }
//
//    private double getNetworkSpeedMbps() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
//        if (connectivityManager != null) {
//            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
//            if (capabilities != null) {
//                return capabilities.getLinkDownstreamBandwidthKbps() / 1000.0;
//            }
//        }
//        return 0.0;
//    }
//
//    private void showNetworkBottomSheet() {
//        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
//        View bottomSheetView = getLayoutInflater().inflate(R.layout.network_bottom_sheet, null);
//        bottomSheetDialog.setContentView(bottomSheetView);
//
//        Button retryButton = bottomSheetView.findViewById(R.id.retryButton);
//        Button exitButton = bottomSheetView.findViewById(R.id.exitButton);
//
//        retryButton.setOnClickListener(v -> retryNetworkCheck(bottomSheetDialog));
//        exitButton.setOnClickListener(v -> finish());
//
//        bottomSheetDialog.setCancelable(false);
//        bottomSheetDialog.show();
//    }
//
//    private void retryNetworkCheck(BottomSheetDialog dialog) {
//        Snackbar.make(parentLayout, "Retrying network check...", Snackbar.LENGTH_SHORT).show();
//
//        new Handler().postDelayed(() -> {
//            if (isNetworkAvailable()) {
//                dialog.dismiss();
//                double speedMbps = getNetworkSpeedMbps();
//                Snackbar.make(parentLayout, "Connected! Speed: " + speedMbps + " Mbps", Snackbar.LENGTH_LONG).show();
//                checkUserLoginStatus();
//            } else {
//                Snackbar.make(parentLayout, "Still no Internet. Retrying...", Snackbar.LENGTH_SHORT).show();
//                retryNetworkCheck(dialog);
//            }
//        }, NETWORK_CHECK_INTERVAL);
//    }
//
//    private void checkUserLoginStatus() {
//        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        boolean isLoggedIn = preferences.getBoolean(KEY_IS_LOGGED_IN, false);
//
//        if (isLoggedIn) {
//            navigateToHomeActivity();
//        } else {
//            navigateToLoginActivity();
//        }
//    }
//
//    private void navigateToHomeActivity() {
//        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
//    private void navigateToLoginActivity() {
//        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
//    private void addTextWithImage(TextView textView) {
//        String text = "Developed by \nsansas.io | ";
//        SpannableString spannable = new SpannableString(text + " ");
//        Drawable drawable = getResources().getDrawable(R.drawable.ig);
//        int drawableSize = (int) 20;
//        drawable.setBounds(0, 0, drawableSize, drawableSize);
//        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
//        spannable.setSpan(imageSpan, text.length(), text.length() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        textView.setText(spannable);

        parentLayout = findViewById(android.R.id.content);

        ImageView introGif = findViewById(R.id.logoImageView);
        TextView introText = findViewById(R.id.splashTextView);
        addTextWithImage(introText);

        Glide.with(this).asBitmap().load(R.mipmap.ic_launcher).into(introGif);

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
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void addTextWithImage(TextView textView) {
        String text = "Developed by \nsansas.io | ";
        SpannableString spannable = new SpannableString(text + " ");
        Drawable drawable = getResources().getDrawable(R.drawable.ig);
        int drawableSize = (int) 20;
        drawable.setBounds(0, 0, drawableSize, drawableSize);
        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        spannable.setSpan(imageSpan, text.length(), text.length() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
    }
}

