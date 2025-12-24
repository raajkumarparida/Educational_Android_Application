package com.sansan.vikashtutorial.Home;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sansan.vikashtutorial.Other.CourseFragment;
import com.sansan.vikashtutorial.Other.LibraryFragment;
import com.sansan.vikashtutorial.Mock.MockFragment;
import com.sansan.vikashtutorial.R;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private static final int REQUEST_NOTIFICATION_PERMISSION = 100;
    private boolean doubleBackToExitPressedOnce = false;
    private static final String PREFS_NAME = "NotificationPrefs";
    private static final String PREF_NOTIFICATION_DENIED = "NotificationDenied";
    private static final String PREF_NOTIFICATION_GRANTED = "NotificationGranted";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        allowNotification();
//        showClassSelectionBottomSheet();


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment.newInstance(), "HOME_FRAGMENT_TAG")
                    .commit();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_course) {
                selectedFragment = new CourseFragment();
            } else if (id == R.id.nav_mock) {
                selectedFragment = new MockFragment();
            } else if (id == R.id.nav_library) {
                selectedFragment = new LibraryFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            return true;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        appInUpdate();
    }




    private void showClassSelectionBottomSheet() {
        // Fetch user ID from FirebaseAuth
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId != null) {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String selectedClass = documentSnapshot.getString("selectedClass");


                        if (selectedClass == null) {
                            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.Theme_VikashTutorial);
                            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_class_selection, null);

                            bottomSheetDialog.setContentView(bottomSheetView);

                            Button btnClass11 = bottomSheetView.findViewById(R.id.btnClass11);
                            Button btnClass12 = bottomSheetView.findViewById(R.id.btnClass12);

                            btnClass11.setOnClickListener(v -> {
                                updateClassSelectionInFirestore(true, false, "Class 11th");
                                bottomSheetDialog.dismiss();
                            });

                            btnClass12.setOnClickListener(v -> {
                                updateClassSelectionInFirestore(false, true, "Class 12th");
                                bottomSheetDialog.dismiss();
                            });

                            bottomSheetDialog.setCancelable(false);
                            bottomSheetDialog.show();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to fetch class selection. Try again.", Toast.LENGTH_SHORT).show()
                    );
        } else {
            Toast.makeText(this, "Please log in to select a class.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateClassSelectionInFirestore(boolean isClass11th, boolean isClass12th, String selectedClass) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> updates = new HashMap<>();
            updates.put("class11th", isClass11th);
            updates.put("class12th", isClass12th);
            updates.put("selectedClass", selectedClass);

            db.collection("users").document(uid)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        SharedPreferences sharedPreferences = getSharedPreferences("CLASS_SELECTION", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("selectedClass", selectedClass);
                        editor.apply();

                        Toast.makeText(this, "Class selection updated to " + selectedClass, Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to update class selection. Try again.", Toast.LENGTH_SHORT).show()
                    );
        }
    }


    private void allowNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            boolean isDenied = prefs.getBoolean(PREF_NOTIFICATION_DENIED, false);
            boolean isGranted = prefs.getBoolean(PREF_NOTIFICATION_GRANTED, false);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                    && !isDenied && !isGranted) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                editor.putBoolean(PREF_NOTIFICATION_GRANTED, true);
                editor.putBoolean(PREF_NOTIFICATION_DENIED, false);
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                editor.putBoolean(PREF_NOTIFICATION_DENIED, true);
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }

            editor.apply();
        }
    }


    private void appInUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.Theme_VikashTutorial);
                View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_update, null);

                bottomSheetDialog.setContentView(bottomSheetView);

                TextView titleTextView = bottomSheetView.findViewById(R.id.titleTextView);
                TextView messageTextView = bottomSheetView.findViewById(R.id.messageTextView);
                titleTextView.setText("Update Vikash Education");
                messageTextView.setText("Hey there! We wanted to let you know that we've just released a new update for our app. This update includes some great new features and improvements, so we highly recommend you update to the latest version as soon as possible. Thanks, and we hope you enjoy the new update!");

                Button updateButton = bottomSheetView.findViewById(R.id.updateButton);
                Button cancelButton = bottomSheetView.findViewById(R.id.cancelButton);

                String packageName = getPackageName();
                String playStoreUrl = "https://play.google.com/store/apps/details?id=" + packageName;

                updateButton.setOnClickListener(v -> {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl)));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl)));
                    }
                    bottomSheetDialog.dismiss();
                });

                cancelButton.setOnClickListener(v -> {
                    bottomSheetDialog.dismiss();
                    finish();
                });

                // Show the BottomSheetDialog
                bottomSheetDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        if (!(currentFragment instanceof HomeFragment)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }
}