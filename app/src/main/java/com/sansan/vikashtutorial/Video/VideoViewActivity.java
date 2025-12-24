package com.sansan.vikashtutorial.Video;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sansan.vikashtutorial.R;

import java.util.HashMap;
import java.util.Map;

public class VideoViewActivity extends AppCompatActivity {

    private StyledPlayerView playerView;
    private ExoPlayer player;
    private ProgressBar progressBar;
        private String videoLink, titleText, descText, thumbnail, length;
    private TextView toolbarTitle;
    private Toolbar toolbar;
    private Handler handler;
    private Runnable hideControlsRunnable;
    private final int AUTO_HIDE_DELAY = 3000;
    private long startTime, endTime;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        playerView = findViewById(R.id.player_view);
        progressBar = findViewById(R.id.loadVideoWithDescription);
        toolbarTitle = findViewById(R.id.videoToolbarText);
        toolbarTitle.setSelected(true);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.deep_moss_green),
                android.graphics.PorterDuff.Mode.SRC_IN);

        videoLink = getIntent().getStringExtra("videoLink");
        titleText = getIntent().getStringExtra("videoTitle");
        thumbnail = getIntent().getStringExtra("videoDesc");
        descText = getIntent().getStringExtra("videoThumbnail");
        length = getIntent().getStringExtra("videoLength");

        playerView.setOnClickListener(v -> toggleToolbarAndControls());
        handler = new Handler();
        hideControlsRunnable = new Runnable() {
            @Override
            public void run() {
                hideToolbarAndControls();
            }
        };


        if (videoLink != null) {
            initializePlayer(videoLink);

            String displayTitle = (titleText == null || titleText.isEmpty()) ? "Untitled Video" : titleText;

            toolbarTitle.setText(displayTitle);
        } else {
            Toast.makeText(this, "Video link is missing", Toast.LENGTH_SHORT).show();
        }


        startTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        endTime = System.currentTimeMillis();
//        long timeSpent = endTime - startTime;
//
//        updateTimeInFirestore(timeSpent);
        handleClassSelection();
    }

    private void handleClassSelection() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean isClass11thSelected = documentSnapshot.getBoolean("class11th");
                        Boolean isClass12thSelected = documentSnapshot.getBoolean("class12th");

                        if (Boolean.TRUE.equals(isClass11thSelected) && Boolean.FALSE.equals(isClass12thSelected)) {
                            handleTimeUpdateClass11th(userId, "class11th");
                        } else if (Boolean.TRUE.equals(isClass12thSelected) && Boolean.FALSE.equals(isClass11thSelected)) {
                            handleTimeUpdateClass12th(userId, "class12th");
                        } else {
                            Toast.makeText(this, "Invalid class selection.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void handleTimeUpdateClass11th(String userId, String className) {
        endTime = System.currentTimeMillis();
        long timeSpent = endTime - startTime;

        String pdfId = titleText;
        String pdfSubId = descText;
        String pdfImg = thumbnail;
        String pdfUrl = videoLink;
        String videoLength = length;

        db.collection("users").document(userId)
                .collection("videoWatchTime")
                .document(pdfId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        long previousTime = snapshot.getLong("timeSpent") != null ? snapshot.getLong("timeSpent") : 0;
                        long updatedTime = previousTime + timeSpent;

                        db.collection("users").document(userId)
                                .collection("videoWatchTime")
                                .document(pdfId)
                                .update("timeSpent", updatedTime, "class", className)
                                .addOnSuccessListener(aVoid -> {
                                    updateTopWatchTimeClass11th(pdfId, timeSpent, className, pdfImg, pdfId, pdfSubId, pdfUrl, length);
                                    updateTotalTimeSpent(timeSpent);
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Error updating PDF watch time", e));
                    } else {
                        db.collection("users").document(userId)
                                .collection("videoWatchTime")
                                .document(pdfId)
                                .set(new HashMap<String, Object>() {{
                                    put("timeSpent", timeSpent);
                                    put("class", className);
                                }})
                                .addOnSuccessListener(aVoid -> {
                                    updateTopWatchTimeClass11th(pdfId, timeSpent, className, pdfImg, pdfId, pdfSubId, pdfUrl, length);
                                    updateTotalTimeSpent(timeSpent);
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Error setting PDF watch time", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching PDF watch time", e));
    }

    private void updateTopWatchTimeClass11th(String pdfTitle, long timeSpent, String className, String img, String topic, String subject, String url, String length) {
        String documentName = className + "Video";
        db.collection("HomeItemsClass11th").document(documentName)
                .get()
                .addOnSuccessListener(snapshot -> {
                    long previousTime = 0;

                    if (snapshot.exists() && snapshot.getLong("timeSpent") != null) {
                        previousTime = snapshot.getLong("timeSpent");
                    }

                    long updatedTime = previousTime + timeSpent;

                    db.collection("HomeItemsClass11th").document(documentName)
                            .set(new HashMap<String, Object>() {{
                                put("timeSpent", updatedTime);
                                put("title", "Resume Watching");
                            }})
                            .addOnSuccessListener(aVoid -> {
                                addPdfItemToClassClass11th(documentName, pdfTitle, img, topic, subject, url, length);
                            })
                            .addOnFailureListener(e -> Log.e("Firestore", "Error updating TopWatchTime for " + className, e));
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching TopWatchTime for " + className, e));
    }

    private void addPdfItemToClassClass11th(String className, String pdfTitle, String img, String topic, String subject, String url, String length) {
        Map<String, Object> pdfItem = new HashMap<>();
        pdfItem.put("imageResource", img);
        pdfItem.put("title", topic);
        pdfItem.put("description", subject);
        pdfItem.put("url", url);
        pdfItem.put("length", length);

        // Add the PDF item to the sub-collection
        db.collection("HomeItemsClass11th").document(className)
                .collection("subItems")
                .document(pdfTitle)
                .set(pdfItem)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "PDF item added successfully for " + pdfTitle))
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding PDF item for " + pdfTitle, e));
    }


    private void handleTimeUpdateClass12th(String userId, String className) {
        endTime = System.currentTimeMillis();
        long timeSpent = endTime - startTime;

        String pdfId = titleText;
        String pdfSubId = descText;
        String pdfImg = thumbnail;
        String pdfUrl = videoLink;

        db.collection("users").document(userId)
                .collection("videoWatchTime")
                .document(pdfId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        long previousTime = snapshot.getLong("timeSpent") != null ? snapshot.getLong("timeSpent") : 0;
                        long updatedTime = previousTime + timeSpent;

                        db.collection("users").document(userId)
                                .collection("videoWatchTime")
                                .document(pdfId)
                                .update("timeSpent", updatedTime, "class", className)
                                .addOnSuccessListener(aVoid -> {
                                    updateTopWatchTimeClass12th(pdfId, timeSpent, className, pdfImg, pdfId, pdfSubId, pdfUrl);
                                    updateTotalTimeSpent(timeSpent);
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Error updating PDF watch time", e));
                    } else {
                        db.collection("users").document(userId)
                                .collection("videoWatchTime")
                                .document(pdfId)
                                .set(new HashMap<String, Object>() {{
                                    put("timeSpent", timeSpent);
                                    put("class", className);
                                }})
                                .addOnSuccessListener(aVoid -> {
                                    updateTopWatchTimeClass12th(pdfId, timeSpent, className, pdfImg, pdfId, pdfSubId, pdfUrl);
                                    updateTotalTimeSpent(timeSpent);
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Error setting PDF watch time", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching PDF watch time", e));
    }

    private void updateTopWatchTimeClass12th(String pdfTitle, long timeSpent, String className, String img, String topic, String subject, String url) {
        String documentName = className + "Video";
        db.collection("HomeItemsClass12th").document(documentName)
                .get()
                .addOnSuccessListener(snapshot -> {
                    long previousTime = 0;

                    if (snapshot.exists() && snapshot.getLong("timeSpent") != null) {
                        previousTime = snapshot.getLong("timeSpent");
                    }

                    long updatedTime = previousTime + timeSpent;

                    db.collection("HomeItemsClass12th").document(documentName)
                            .set(new HashMap<String, Object>() {{
                                put("timeSpent", updatedTime);
                                put("title", "Top Video");
                            }})
                            .addOnSuccessListener(aVoid -> {
                                addPdfItemToClassClass12th(documentName, pdfTitle, img, topic, subject, url);
                            })
                            .addOnFailureListener(e -> Log.e("Firestore", "Error updating TopWatchTime for " + className, e));
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching TopWatchTime for " + className, e));
    }

    private void addPdfItemToClassClass12th(String className, String pdfTitle, String img, String topic, String subject, String url) {
        Map<String, Object> pdfItem = new HashMap<>();
        pdfItem.put("imageResource", img);
        pdfItem.put("title", topic);
        pdfItem.put("description", subject);
        pdfItem.put("url", url);

        // Add the PDF item to the sub-collection
        db.collection("HomeItemsClass12th").document(className)
                .collection("subItems")
                .document(pdfTitle)
                .set(pdfItem)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "PDF item added successfully for " + pdfTitle))
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding PDF item for " + pdfTitle, e));
    }

    private void updateTotalTimeSpent(long timeSpent) {
        db.collection("TotalWatchedTime").document("TotalPdfTimeSpent")
                .get()
                .addOnSuccessListener(snapshot -> {
                    long previousTime = 0;

                    if (snapshot.exists() && snapshot.getLong("timeSpent") != null) {
                        previousTime = snapshot.getLong("timeSpent");
                    }

                    long updatedTime = previousTime + timeSpent;

                    db.collection("TotalWatchedTime").document("TotalPdfTimeSpent")
                            .set(new HashMap<String, Object>() {{
                                put("timeSpent", updatedTime);
                            }})
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "TotalTimeSpent updated successfully"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error updating TotalTimeSpent", e));
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching TotalTimeSpent", e));
    }

//    private void updateTimeInFirestore(long timeSpent) {
//        String userId = mAuth.getCurrentUser().getUid();
//        String pdfId = titleText;
//
//        if (userId != null) {
//            db.collection("users").document(userId)
//                    .collection("videoWatchTime")
//                    .document(pdfId)
//                    .get()
//                    .addOnSuccessListener(snapshot -> {
//                        if (snapshot.exists()) {
//                            long previousTime = snapshot.getLong("timeSpent") != null ? snapshot.getLong("timeSpent") : 0;
//                            long updatedTime = previousTime + timeSpent;
//
//                            db.collection("users").document(userId)
//                                    .collection("videoWatchTime")
//                                    .document(pdfId)
//                                    .update("timeSpent", updatedTime)
//                                    .addOnSuccessListener(aVoid -> {
//                                    })
//                                    .addOnFailureListener(e -> {
//                                    });
//                        } else {
//                            db.collection("users").document(userId)
//                                    .collection("videoWatchTime")
//                                    .document(pdfId)
//                                    .set(new HashMap<String, Object>() {{
//                                        put("timeSpent", timeSpent);
//                                    }})
//                                    .addOnSuccessListener(aVoid -> {
//                                        updateTopWatchTime(pdfId, timeSpent);
//                                        updateTotalTimeSpent(timeSpent);
//                                    })
//                                    .addOnFailureListener(e -> {
//
//                                    });
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//
//                    });
//        }
//    }
//
//    private void updateTopWatchTime(String pdfTitle, long timeSpent) {
//        db.collection("TopVideoWatchTime").document(pdfTitle)
//                .get()
//                .addOnSuccessListener(snapshot -> {
//                    long previousTime = 0;
//
//                    if (snapshot.exists() && snapshot.getLong("timeSpent") != null) {
//                        previousTime = snapshot.getLong("timeSpent");
//                    }
//
//                    long updatedTime = previousTime + timeSpent;
//
//                    db.collection("TopVideoWatchTime").document(pdfTitle)
//                            .set(new HashMap<String, Object>() {{
//                                put("timeSpent", updatedTime);
//                            }})
//                            .addOnSuccessListener(aVoid -> {
//                                Log.d("Firestore", "TopWatchTime updated successfully");
//                            })
//                            .addOnFailureListener(e -> {
//                                Log.e("Firestore", "Error updating TopWatchTime", e);
//                            });
//                })
//                .addOnFailureListener(e -> {
//                    Log.e("Firestore", "Error fetching TopWatchTime", e);
//                });
//    }
//
//    private void updateTotalTimeSpent(long timeSpent) {
//        db.collection("TotalWatchedTime").document("TotalVideoTimeSpent")
//                .get()
//                .addOnSuccessListener(snapshot -> {
//                    long previousTime = 0;
//
//                    if (snapshot.exists() && snapshot.getLong("timeSpent") != null) {
//                        previousTime = snapshot.getLong("timeSpent");
//                    }
//
//                    long updatedTime = previousTime + timeSpent;
//
//
//                    db.collection("TotalWatchedTime").document("TotalVideoTimeSpent")
//                            .set(new HashMap<String, Object>() {{
//                                put("timeSpent", updatedTime);
//                            }})
//                            .addOnSuccessListener(aVoid -> {
//                                Log.d("Firestore", "TotalTimeSpent updated successfully");
//                            })
//                            .addOnFailureListener(e -> {
//                                Log.e("Firestore", "Error updating TotalTimeSpent", e);
//                            });
//                })
//                .addOnFailureListener(e -> {
//                    Log.e("Firestore", "Error fetching TotalTimeSpent", e);
//                });
//    }

    private void initializePlayer(String videoUrl) {

        LoadControl loadControl = new DefaultLoadControl.Builder()
                .setAllocator(new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
                .setBufferDurationsMs(
                        50000,
                        100000,
                        1500,
                        3000
                )
                .setTargetBufferBytes(-1)
                .setPrioritizeTimeOverSizeThresholds(true)
                .build();

        player = new ExoPlayer.Builder(this)
                .setLoadControl(loadControl)
                .build();

        playerView.setPlayer(player);

        Uri videoUri = Uri.parse(videoUrl);
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        player.setMediaItem(mediaItem);
        player.prepare();

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                    player.play();
                } else if (playbackState == Player.STATE_BUFFERING) {
                    progressBar.setVisibility(View.VISIBLE);
                } else if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                progressBar.setVisibility(View.GONE);
                String errorMessage = "Error playing video: " + error.getMessage();
                Toast.makeText(VideoViewActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleToolbarAndControls() {
        if (toolbar.getVisibility() == View.VISIBLE) {
            hideToolbarAndControls();
        } else {
            showToolbarAndControls();
            // Hide them again after a delay
            handler.postDelayed(hideControlsRunnable, AUTO_HIDE_DELAY);
        }
    }

    private void showToolbarAndControls() {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setAlpha(0.0f);
        toolbar.animate().alpha(1.0f).setDuration(300).start();

        // Show ExoPlayer controls
        playerView.showController();
    }

    private void hideToolbarAndControls() {
        toolbar.animate().alpha(0.0f).setDuration(300).withEndAction(() -> toolbar.setVisibility(View.GONE)).start();

        // Hide ExoPlayer controls
        playerView.hideController();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (player != null) {
            player.prepare();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null && player.getPlaybackState() == Player.STATE_READY) {
            player.play();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}