package com.sansan.vikashtutorial.DrawerActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sansan.vikashtutorial.R;

import java.util.ArrayList;
import java.util.List;

public class CenterActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private FirebaseFirestore db;
    private ImageSlider firstYearSlider, secondYearSlider, librarySlider, functionSlider, environmentSlider;
    private List<SlideModel> firstYearSliderSlideModels, secondYearSliderSlideModels, librarySliderSlideModels, functionSliderSlideModels, environmentSliderSlideModels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center);

        db = FirebaseFirestore.getInstance();

        TextView toolbarText = findViewById(R.id.toolbarText);
        toolbarText.setSelected(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading images...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView updownTextView = findViewById(R.id.click);
        TranslateAnimation upDownAnimation = new TranslateAnimation(
                0, 0,
                0, 100
        );
        upDownAnimation.setDuration(1000);
        upDownAnimation.setRepeatCount(Animation.INFINITE);
        upDownAnimation.setRepeatMode(Animation.REVERSE);

        updownTextView.startAnimation(upDownAnimation);

        firstYearSlider = findViewById(R.id.firstYearSlider);
        firstYearSliderSlideModels = new ArrayList<>();
        fetchFirstYearSlidingImagesFromFirestore();

        secondYearSlider = findViewById(R.id.secondYearSlider);
        secondYearSliderSlideModels = new ArrayList<>();
        fetchSecondYearSlidingImagesFromFirestore();

        librarySlider = findViewById(R.id.librarySlider);
        librarySliderSlideModels = new ArrayList<>();
        fetchLibrarySlidingImagesFromFirestore();

        functionSlider = findViewById(R.id.functionSlider);
        functionSliderSlideModels = new ArrayList<>();
        fetchFunctionSlidingImagesFromFirestore();

        environmentSlider = findViewById(R.id.environmentSlider);
        environmentSliderSlideModels = new ArrayList<>();
        fetchEnvironmentSlidingImagesFromFirestore();

        FloatingActionButton fabOpenMap = findViewById(R.id.locate);

        fabOpenMap.setOnClickListener(view -> {
            String locationUrl = "https://www.google.com/maps/place/Vikash+Tutorial/@19.605517,85.0703167,122m/data=!3m1!1e3!4m6!3m5!1s0x3a1805a4aa2ce0f3:0x73fe606f94918a8c!8m2!3d19.6055823!4d85.0743532!16s%2Fg%2F11g4cym8xj?entry=ttu&g_ep=EgoyMDI0MTExNy4wIKXMDSoASAFQAw%3D%3D";

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationUrl));
            intent.setPackage("com.google.android.apps.maps");

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationUrl));
                startActivity(fallbackIntent);
            }
        });
    }

    private void fetchFirstYearSlidingImagesFromFirestore() {
        progressDialog.show();
        db.collection("SLIDINGIMAGES")
                .get()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            String imageUrl = document.getString("url");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                firstYearSliderSlideModels.add(new SlideModel(imageUrl, ScaleTypes.CENTER_CROP));
                            } else {
                                Log.e("Firestore", "Invalid imageUrl found in Firestore");
                            }
                        }
                        if (!firstYearSliderSlideModels.isEmpty()) {
                            firstYearSlider.setImageList(firstYearSliderSlideModels, ScaleTypes.CENTER_CROP);
                        }
                    } else {
                        Log.e("Firestore Error", "Error fetching data", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(CenterActivity.this, "Error loading images", Toast.LENGTH_SHORT).show();
                });
    }

        private void fetchSecondYearSlidingImagesFromFirestore() {
            progressDialog.show();
            db.collection("SLIDINGIMAGES")
                    .get()
                    .addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                String imageUrl = document.getString("url");
                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    secondYearSliderSlideModels.add(new SlideModel(imageUrl, ScaleTypes.CENTER_CROP));
                                } else {
                                    Log.e("Firestore", "Invalid imageUrl found in Firestore");
                                }
                            }
                            if (!secondYearSliderSlideModels.isEmpty()) {
                                secondYearSlider.setImageList(secondYearSliderSlideModels, ScaleTypes.CENTER_CROP);
                            }
                        } else {
                            Log.e("Firestore Error", "Error fetching data", task.getException());
                        }
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(CenterActivity.this, "Error loading images", Toast.LENGTH_SHORT).show();
                    });
        }
            private void fetchLibrarySlidingImagesFromFirestore() {
                progressDialog.show();
                db.collection("SLIDINGIMAGES")
                        .get()
                        .addOnCompleteListener(task -> {
                            progressDialog.dismiss();
                            if (task.isSuccessful() && task.getResult() != null) {
                                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                    String imageUrl = document.getString("url");
                                    if (imageUrl != null && !imageUrl.isEmpty()) {
                                        librarySliderSlideModels.add(new SlideModel(imageUrl, ScaleTypes.CENTER_CROP));
                                    } else {
                                        Log.e("Firestore", "Invalid imageUrl found in Firestore");
                                    }
                                }
                                if (!librarySliderSlideModels.isEmpty()) {
                                    librarySlider.setImageList(librarySliderSlideModels, ScaleTypes.CENTER_CROP);
                                }
                            } else {
                                Log.e("Firestore Error", "Error fetching data", task.getException());
                            }
                        })
                        .addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(CenterActivity.this, "Error loading images", Toast.LENGTH_SHORT).show();
                        });
            }

                private void fetchFunctionSlidingImagesFromFirestore () {
                    progressDialog.show();
                    db.collection("SLIDINGIMAGES")
                            .get()
                            .addOnCompleteListener(task -> {
                                progressDialog.dismiss();
                                if (task.isSuccessful() && task.getResult() != null) {
                                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                        String imageUrl = document.getString("url");
                                        if (imageUrl != null && !imageUrl.isEmpty()) {
                                            functionSliderSlideModels.add(new SlideModel(imageUrl, ScaleTypes.CENTER_CROP));
                                        } else {
                                            Log.e("Firestore", "Invalid imageUrl found in Firestore");
                                        }
                                    }
                                    if (!functionSliderSlideModels.isEmpty()) {
                                        functionSlider.setImageList(functionSliderSlideModels, ScaleTypes.CENTER_CROP);
                                    }
                                } else {
                                    Log.e("Firestore Error", "Error fetching data", task.getException());
                                }
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(CenterActivity.this, "Error loading images", Toast.LENGTH_SHORT).show();
                            });
                }

                    private void fetchEnvironmentSlidingImagesFromFirestore () {
                        progressDialog.show();
                        db.collection("SLIDINGIMAGES")
                                .get()
                                .addOnCompleteListener(task -> {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                            String imageUrl = document.getString("url");
                                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                                environmentSliderSlideModels.add(new SlideModel(imageUrl, ScaleTypes.CENTER_CROP));
                                            } else {
                                                Log.e("Firestore", "Invalid imageUrl found in Firestore");
                                            }
                                        }
                                        if (!environmentSliderSlideModels.isEmpty()) {
                                            environmentSlider.setImageList(environmentSliderSlideModels, ScaleTypes.CENTER_CROP);
                                        }
                                    } else {

                                        Log.e("Firestore Error", "Error fetching data", task.getException());
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(CenterActivity.this, "Error loading images", Toast.LENGTH_SHORT).show();
                                });
                    }

        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}