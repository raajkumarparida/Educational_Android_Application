package com.sansan.vikashtutorial.Pdf;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sansan.vikashtutorial.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PdfViewActivity extends AppCompatActivity {

    private ImageButton bookmarkButton;
    private PDFView notePdfView;
    private ProgressDialog progressDialog;
    private String url, title, subTitle, img;
    private long startTime, endTime;
    private ImageView noPdfImg;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        bookmarkButton = findViewById(R.id.bookmarkNoteBtn);
        noPdfImg = findViewById(R.id.noPdfImage);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        notePdfView = findViewById(R.id.noteView);
        TextView pdfTitle = findViewById(R.id.titlePdfText);
        pdfTitle.setSelected(true);

        Intent intent = getIntent();
        if (intent != null) {
            title = intent.getStringExtra("pdfTopic");
            url = intent.getStringExtra("pdfUrl");
            img = intent.getStringExtra("pdfImg");
            subTitle = intent.getStringExtra("pdfSub");

            pdfTitle.setText(title);

            if (url != null && !url.isEmpty()) {
                progressDialog.show();
                new RetrievePDFStream().execute(url);
            } else {
                Toast.makeText(this, "No PDF URL provided", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "No Intent received", Toast.LENGTH_SHORT).show();
            finish();
        }

        updateBookmarkIcon();
        bookmarkButton.setOnClickListener(v -> handleBookmarkAction());
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        endTime = System.currentTimeMillis();
        long timeSpent = endTime - startTime;

        updateTimeInFirestore(timeSpent);
//        handleClassSelection();
    }

    private void updateTimeInFirestore(long timeSpent) {
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String pdfId = getIntent().getStringExtra("pdfTopic");

        if (pdfId == null || pdfId.isEmpty()) {
            Toast.makeText(this, "Invalid PDF Topic", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference pdfWatchDocRef = db.collection("users")
                .document(userId)
                .collection("pdfWatchTime")
                .document(pdfId); // Ensure even segments

        pdfWatchDocRef.get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        long previousTime = snapshot.getLong("timeSpent") != null ? snapshot.getLong("timeSpent") : 0;
                        long updatedTime = previousTime + timeSpent;

                        pdfWatchDocRef.update("timeSpent", updatedTime)
                                .addOnSuccessListener(aVoid -> {
                                    // Successfully updated
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure
                                });
                    } else {
                        Map<String, Object> timeData = new HashMap<>();
                        timeData.put("timeSpent", timeSpent);

                        pdfWatchDocRef.set(timeData)
                                .addOnSuccessListener(aVoid -> {
                                    // Successfully added
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }



//    private void handleClassSelection() {
//        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
//                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
//
//        if (userId == null) {
//            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        db.collection("users").document(userId).get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        Boolean isClass11thSelected = documentSnapshot.getBoolean("class11th");
//                        Boolean isClass12thSelected = documentSnapshot.getBoolean("class12th");
//
//                        if (Boolean.TRUE.equals(isClass11thSelected) && Boolean.FALSE.equals(isClass12thSelected)) {
//                            handleTimeUpdateClass11th(userId, "class11th");
//                        } else if (Boolean.TRUE.equals(isClass12thSelected) && Boolean.FALSE.equals(isClass11thSelected)) {
//                            handleTimeUpdateClass12th(userId, "class12th");
//                        } else {
//                            Toast.makeText(this, "Invalid class selection.", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//    }
//
//    private void handleTimeUpdateClass11th(String userId, String className) {
//        endTime = System.currentTimeMillis();
//        long timeSpent = endTime - startTime;
//
//        String pdfId = title;
//        String pdfSubId = subTitle;
//        String pdfImg = img;
//        String pdfUrl = url;
//
//        db.collection("users").document(userId)
//                .collection("pdfWatchTime")
//                .document(pdfId)
//                .get()
//                .addOnSuccessListener(snapshot -> {
//                    if (snapshot.exists()) {
//                        long previousTime = snapshot.getLong("timeSpent") != null ? snapshot.getLong("timeSpent") : 0;
//                        long updatedTime = previousTime + timeSpent;
//
//                        db.collection("users").document(userId)
//                                .collection("pdfWatchTime")
//                                .document(pdfId)
//                                .update("timeSpent", updatedTime, "class", className)
//                                .addOnSuccessListener(aVoid -> {
//                                    updateTopWatchTimeClass11th(pdfId, timeSpent, className, pdfImg, pdfId, pdfSubId, pdfUrl);
//                                    updateTotalTimeSpent(timeSpent);
//
//                                })
//                                .addOnFailureListener(e -> Log.e("Firestore", "Error updating PDF watch time", e));
//                    } else {
//                        db.collection("users").document(userId)
//                                .collection("pdfWatchTime")
//                                .document(pdfId)
//                                .set(new HashMap<String, Object>() {{
//                                    put("timeSpent", timeSpent);
//                                    put("class", className);
//                                }})
//                                .addOnSuccessListener(aVoid -> {
//                                    updateTopWatchTimeClass11th(pdfId, timeSpent, className, pdfImg, pdfId, pdfSubId, pdfUrl);
//                                    updateTotalTimeSpent(timeSpent);
//                                })
//                                .addOnFailureListener(e -> Log.e("Firestore", "Error setting PDF watch time", e));
//                    }
//                })
//                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching PDF watch time", e));
//    }
//
//
//    private void updateTopWatchTimeClass11th(String pdfTitle, long timeSpent, String className, String img, String topic, String subject, String url) {
//        String documentName = className + "Pdf";
//        db.collection("HomeItemsClass11th").document(documentName)
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
//                    db.collection("HomeItemsClass11th").document(documentName)
//                            .set(new HashMap<String, Object>() {{
//                                put("timeSpent", updatedTime);
//                                put("title", "Resume Reading");
//                            }})
//                            .addOnSuccessListener(aVoid -> {
//                                addPdfItemToClass11th(documentName, pdfTitle, img, topic, subject, url, updatedTime);
//                            })
//                            .addOnFailureListener(e -> Log.e("Firestore", "Error updating TopWatchTime for " + className, e));
//                })
//                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching TopWatchTime for " + className, e));
//    }
//
//    private void addPdfItemToClass11th(String className, String pdfTitle, String img, String topic, String subject, String url, long timeSpent) {
//        db.collection("HomeItemsClass11th").document(className)
//                .collection("subItems")
//                .document(pdfTitle)
//                .get()
//                .addOnSuccessListener(snapshot -> {
//                    long previousTime = 0;
//
//                    // Fetch the existing timeSpent if the document exists
//                    if (snapshot.exists() && snapshot.getLong("timeSpent") != null) {
//                        previousTime = snapshot.getLong("timeSpent");
//                    }
//
//                    // Calculate the updated time
//                    long updatedTime = previousTime + timeSpent;
//
//                    // Create the PDF item data
//                    Map<String, Object> pdfItem = new HashMap<>();
//                    pdfItem.put("timeSpent", updatedTime);
//                    pdfItem.put("imageResource", img);
//                    pdfItem.put("title", topic);
//                    pdfItem.put("description", subject);
//                    pdfItem.put("url", url);
//
//                    // Update or set the PDF item in the sub-collection
//                    db.collection("HomeItemsClass11th").document(className)
//                            .collection("subItems")
//                            .document(pdfTitle)
//                            .set(pdfItem)
//                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "PDF item updated successfully for " + pdfTitle))
//                            .addOnFailureListener(e -> Log.e("Firestore", "Error updating PDF item for " + pdfTitle, e));
//                })
//                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching PDF item for " + pdfTitle, e));
//    }
//
//    private void handleTimeUpdateClass12th(String userId, String className) {
//        endTime = System.currentTimeMillis();
//        long timeSpent = endTime - startTime;
//
//        String pdfId = title;
//        String pdfSubId = subTitle;
//        String pdfImg = img;
//        String pdfUrl = url;
//
//        db.collection("users").document(userId)
//                .collection("pdfWatchTime")
//                .document(pdfId)
//                .get()
//                .addOnSuccessListener(snapshot -> {
//                    if (snapshot.exists()) {
//                        long previousTime = snapshot.getLong("timeSpent") != null ? snapshot.getLong("timeSpent") : 0;
//                        long updatedTime = previousTime + timeSpent;
//
//                        db.collection("users").document(userId)
//                                .collection("pdfWatchTime")
//                                .document(pdfId)
//                                .update("timeSpent", updatedTime, "class", className)
//                                .addOnSuccessListener(aVoid -> {
//                                    updateTopWatchTimeClass12th(pdfId, timeSpent, className, pdfImg, pdfId, pdfSubId, pdfUrl);
//                                    updateTotalTimeSpent(timeSpent);
//
//                                })
//                                .addOnFailureListener(e -> Log.e("Firestore", "Error updating PDF watch time", e));
//                    } else {
//                        db.collection("users").document(userId)
//                                .collection("pdfWatchTime")
//                                .document(pdfId)
//                                .set(new HashMap<String, Object>() {{
//                                    put("timeSpent", timeSpent);
//                                    put("class", className);
//                                }})
//                                .addOnSuccessListener(aVoid -> {
//                                    updateTopWatchTimeClass12th(pdfId, timeSpent, className, pdfImg, pdfId, pdfSubId, pdfUrl);
//                                    updateTotalTimeSpent(timeSpent);
//                                })
//                                .addOnFailureListener(e -> Log.e("Firestore", "Error setting PDF watch time", e));
//                    }
//                })
//                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching PDF watch time", e));
//    }
//
//
//    private void updateTopWatchTimeClass12th(String pdfTitle, long timeSpent, String className, String img, String topic, String subject, String url) {
//        String documentName = className + "Pdf";
//        db.collection("HomeItemsClass12th").document(documentName)
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
//                    db.collection("HomeItemsClass12th").document(documentName)
//                            .set(new HashMap<String, Object>() {{
//                                put("timeSpent", updatedTime);
//                                put("title", "Resume Reading");
//                            }})
//                            .addOnSuccessListener(aVoid -> {
//                                addPdfItemToClass12th(documentName, pdfTitle, img, topic, subject, url, updatedTime);
//                            })
//                            .addOnFailureListener(e -> Log.e("Firestore", "Error updating TopWatchTime for " + className, e));
//                })
//                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching TopWatchTime for " + className, e));
//    }
//
//    private void addPdfItemToClass12th(String className, String pdfTitle, String img, String topic, String subject, String url, long timeSpent) {
//        db.collection("HomeItemsClass12th").document(className)
//                .collection("subItems")
//                .document(pdfTitle)
//                .get()
//                .addOnSuccessListener(snapshot -> {
//                    long previousTime = 0;
//
//                    // Fetch the existing timeSpent if the document exists
//                    if (snapshot.exists() && snapshot.getLong("timeSpent") != null) {
//                        previousTime = snapshot.getLong("timeSpent");
//                    }
//
//                    // Calculate the updated time
//                    long updatedTime = previousTime + timeSpent;
//
//                    // Create the PDF item data
//                    Map<String, Object> pdfItem = new HashMap<>();
//                    pdfItem.put("timeSpent", updatedTime);
//                    pdfItem.put("imageResource", img);
//                    pdfItem.put("title", topic);
//                    pdfItem.put("description", subject);
//                    pdfItem.put("url", url);
//
//                    // Update or set the PDF item in the sub-collection
//                    db.collection("HomeItemsClass12th").document(className)
//                            .collection("subItems")
//                            .document(pdfTitle)
//                            .set(pdfItem)
//                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "PDF item updated successfully for " + pdfTitle))
//                            .addOnFailureListener(e -> Log.e("Firestore", "Error updating PDF item for " + pdfTitle, e));
//                })
//                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching PDF item for " + pdfTitle, e));
//    }
//
//    private void updateTotalTimeSpent(long timeSpent) {
//        db.collection("TotalWatchedTime").document("TotalPdfTimeSpent")
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
//                    db.collection("TotalWatchedTime").document("TotalPdfTimeSpent")
//                            .set(new HashMap<String, Object>() {{
//                                put("timeSpent", updatedTime);
//                            }})
//                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "TotalTimeSpent updated successfully"))
//                            .addOnFailureListener(e -> Log.e("Firestore", "Error updating TotalTimeSpent", e));
//                })
//                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching TotalTimeSpent", e));
//    }


    private void updateBookmarkIcon() {
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        CollectionReference userBookmarksRef = db.collection("users")
                .document(userId)
                .collection("NoteBookmarks");

        progressDialog.show();

        userBookmarksRef.whereEqualTo("bookedUrl", url)
                .get()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null && !snapshot.isEmpty()) {
                            bookmarkButton.setImageResource(R.drawable.favorait); // Mark as bookmarked
                        } else {
                            bookmarkButton.setImageResource(R.drawable.unfavorait); // Mark as not bookmarked
                        }
                    } else {
                        Toast.makeText(this, "Error checking bookmark: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleBookmarkAction() {
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        CollectionReference userBookmarksRef = db.collection("users")
                .document(userId)
                .collection("NoteBookmarks");

        progressDialog.show();

        userBookmarksRef.whereEqualTo("bookedUrl", url)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null && !snapshot.isEmpty()) {
                            for (DocumentSnapshot document : snapshot.getDocuments()) {
                                userBookmarksRef.document(document.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Bookmark removed", Toast.LENGTH_SHORT).show();
                                            bookmarkButton.setImageResource(R.drawable.unfavorait);
                                            progressDialog.dismiss();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Failed to remove bookmark: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        });
                            }
                        } else {
                            Map<String, Object> bookmarkMap = new HashMap<>();
                            bookmarkMap.put("bookedImg", img);
                            bookmarkMap.put("bookedTopic", title);
                            bookmarkMap.put("bookedUrl", url);

                            userBookmarksRef.add(bookmarkMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Bookmark added", Toast.LENGTH_SHORT).show();
                                        bookmarkButton.setImageResource(R.drawable.favorait);
                                        progressDialog.dismiss();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to add bookmark: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Error handling bookmark: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
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

    private class RetrievePDFStream extends AsyncTask<String, Void, InputStream> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            if (inputStream != null) {
                notePdfView.fromStream(inputStream)
                        .enableSwipe(true)
                        .scrollHandle(new DefaultScrollHandle(PdfViewActivity.this))
                        .onLoad(nbPages -> {
                            progressDialog.dismiss(); // Dismiss only when fully loaded
                        })
                        .onError(t -> {
                            progressDialog.dismiss();
                            Toast.makeText(PdfViewActivity.this, "Failed to load PDF", Toast.LENGTH_SHORT).show();
                            noPdfImg.setVisibility(View.VISIBLE);
                        })
                        .load();
            } else {
                progressDialog.dismiss();
                Toast.makeText(PdfViewActivity.this, "Failed to load PDF", Toast.LENGTH_SHORT).show();
                noPdfImg.setVisibility(View.VISIBLE);
            }
        }
    }
}