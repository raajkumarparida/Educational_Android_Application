package com.sansan.vikashtutorial.Home;


import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.search.SearchBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sansan.vikashtutorial.DrawerActivity.AboutActivity;
import com.sansan.vikashtutorial.DrawerActivity.BookmarksActivity;
import com.sansan.vikashtutorial.DrawerActivity.CenterActivity;
import com.sansan.vikashtutorial.HomeVideoSubItem;
import com.sansan.vikashtutorial.Other.CustomBottomSheet;
import com.sansan.vikashtutorial.Activity.LoginActivity;
import com.sansan.vikashtutorial.DrawerActivity.OurResultActivity;
import com.sansan.vikashtutorial.DrawerActivity.QuickLearningActivity;
import com.sansan.vikashtutorial.R;
import com.sansan.vikashtutorial.DrawerActivity.ScholarshipActivity;
import com.sansan.vikashtutorial.Search.SearchPdfActivity;
import com.sansan.vikashtutorial.DrawerActivity.YourExamActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private RadioGroup navRadioGroup;
    private SearchBar searchBar;
    private TextView greetText, userName, navHeaderEmail, navHeaderUserName, navVideoWatchTime, navPdfWatchTime, navMockSpendTime, navLoginSince, navHeaderEditBtn;
    private ImageView userImg, navHeaderImg;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference mStore;
    private ListenerRegistration userListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private ImageSlider imageSlider;
    private List<SlideModel> slideModels;
    private Handler handler;
    private Runnable hintUpdater;
    private String[] hints = {"Search Physics...", "Look up for Mathematics...", "Find Solution Videos...", "Explore all Courses..."};
    private int currentHintIndex = 0;
    private RecyclerView rcvTop, rcvBtm;
    private ItemHeaderAdapter itemAdapter;
    private HomeContentAdapter contentAdapter;
    private RatingBar ratingBar;
    private Button submitButton;
    private ReviewManager reviewManager;
    private Switch switchCompat;
    private DrawerLayout drawerLayout;
    private boolean nightMode;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private FrameLayout frameLayout;
    private NestedScrollView nestedScrollView;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        initializeUIElements(view);

        if (currentUser != null) {
            String userId = currentUser.getUid();
            mStore = FirebaseStorage.getInstance().getReference();
            fetchUserData(userId);
        } else {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }

        showProgressDialog();
        setupEventListeners();
        startHintCycling();
        setupNavigationDrawer(view);
        setupToolbar(view);
        setupGreetingMessage();
        setupSwipeRefreshLayout();
        fetchSlidingImagesFromFirestore();
//        fetchItemRecyclerViewData();
        fetchContentRecyclerViewData();
        handleClassSelection();
//        fetchTopPdfRecyclerViewData();

        return view;

    }

    private void handleClassSelection() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) {
            if (isAdded()) {
                Toast.makeText(getActivity(), "User not logged in.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!isAdded()) {
                        // Fragment is not attached, safely exit
                        return;
                    }

                    if (documentSnapshot.exists()) {
                        String selectedClass = documentSnapshot.getString("selectedClass");

                        if ("Class 11th".equals(selectedClass)) {
                            fetchPdfItemRecyclerViewData11th();
                        } else if ("Class 12th".equals(selectedClass)) {
                            fetchPdfItemRecyclerViewData12th();
                        } else {
                            Toast.makeText(getActivity(), "Please select a valid class.", Toast.LENGTH_SHORT).show();
                            showClassSelectionDialog(getView());
                        }
                    } else {
                        Toast.makeText(getActivity(), "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) {
                        // Fragment is not attached, safely exit
                        return;
                    }
                    Log.e("FirestoreError", "Failed to fetch user data", e);
                    Toast.makeText(getActivity(), "Failed to fetch user data. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void showClassSelectionDialog(View view) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Snackbar.make(view, "Please log in to select a class.", Snackbar.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String selectedClass = documentSnapshot.getString("selectedClass");

                    if (selectedClass == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setTitle("Select Your Class");
                        builder.setMessage("Please choose your class.");

                        builder.setPositiveButton("Class 11th", (dialog, which) -> {
                            updateClassSelectionForUser(true, false, "Class 11th", view);
                            dialog.dismiss();
                        });

                        builder.setNegativeButton("Class 12th", (dialog, which) -> {
                            updateClassSelectionForUser(false, true, "Class 12th", view);
                            dialog.dismiss();
                        });

                        builder.setCancelable(false);
                        builder.show();
                    }
                })
                .addOnFailureListener(e ->
                        Snackbar.make(view, "Failed to fetch class selection. Try again.", Snackbar.LENGTH_SHORT).show()
                );
    }

    private void updateClassSelectionForUser(boolean isClass11th, boolean isClass12th, String selectedClass, View view) {
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
                        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("CLASS_SELECTION", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("selectedClass", selectedClass);
                        editor.apply();
                        Snackbar.make(view, "Class selection updated to " + selectedClass, Snackbar.LENGTH_SHORT).show();
                        restartHomeFragment(drawerLayout);
                    })
                    .addOnFailureListener(e ->
                            Snackbar.make(view, "Failed to update class selection. Try again.", Snackbar.LENGTH_SHORT).show()
                    );
        }
    }


//    private void showClassSelectionBottomSheet() {
//        // Fetch user ID from FirebaseAuth
//        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
//                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
//
//        if (userId != null) {
//            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//            firestore.collection("users").document(userId).get()
//                    .addOnSuccessListener(documentSnapshot -> {
//                        String selectedClass = documentSnapshot.getString("selectedClass");
//
//                            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity(), R.style.Theme_VikashTutorial);
//                            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_class_selection, null);
//
//                            bottomSheetDialog.setContentView(bottomSheetView);
//
//                            Button btnClass11 = bottomSheetView.findViewById(R.id.btnClass11);
//                            Button btnClass12 = bottomSheetView.findViewById(R.id.btnClass12);
//
//                            btnClass11.setOnClickListener(v -> {
//                                updateClassSelectionInFirestore(true, false, "Class 11th");
//                                bottomSheetDialog.dismiss();
//                            });
//
//                            btnClass12.setOnClickListener(v -> {
//                                updateClassSelectionInFirestore(false, true, "Class 12th");
//                                bottomSheetDialog.dismiss();
//                            });
//
//                            bottomSheetDialog.setCancelable(false);
//                            bottomSheetDialog.show();
//                    })
//                    .addOnFailureListener(e ->
//                            Toast.makeText(requireContext(), "Failed to fetch class selection. Try again.", Toast.LENGTH_SHORT).show()
//                    );
//        } else {
//            Toast.makeText(requireContext(), "Please log in to select a class.", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void initializeUIElements(View view) {

        Animation top_to_btm = AnimationUtils.loadAnimation(getActivity(), R.anim.top_to_btm);
        Animation btm_to_top = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_animation);

        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        nestedScrollView.setAnimation(btm_to_top);
        frameLayout = view.findViewById(R.id.frameLayout);
        frameLayout.setAnimation(top_to_btm);
        imageSlider = view.findViewById(R.id.slider);
        greetText = view.findViewById(R.id.homeTextGmGe);
        greetText.setSelected(true);
        userName = view.findViewById(R.id.homeUserName);
        userImg = view.findViewById(R.id.homeUserImg);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        searchBar = view.findViewById(R.id.searchBar);
        searchBar.startAnimation(btm_to_top);
        handler = new Handler(Looper.getMainLooper());

        rcvTop = view.findViewById(R.id.homeTopRecyclerview);
        rcvTop.setLayoutManager(new LinearLayoutManager(getActivity()));

        rcvBtm = view.findViewById(R.id.homeBottomRecyclerview);
        rcvBtm.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        reviewManager = ReviewManagerFactory.create(getActivity());
        ratingBar = view.findViewById(R.id.ratingBar);
        submitButton = view.findViewById(R.id.submitButton);

        slideModels = new ArrayList<>();
    }

    private void setupEventListeners() {
        searchBar.setOnClickListener(v -> startActivity(new Intent(getActivity(), SearchPdfActivity.class)));

        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            if (rating > 0) {
                submitRating(rating);
            } else {
                Toast.makeText(getActivity(), "Please provide a rating", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitRating(float rating) {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "anonymous";


        db.collection("appRatings")
                .whereEqualTo("uid", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {

                        Toast.makeText(getActivity(), "You have already submitted a rating.", Toast.LENGTH_SHORT).show();
                    } else {

                        Map<String, Object> ratingData = new HashMap<>();
                        ratingData.put("uid", userId);
                        ratingData.put("rating", rating);
                        ratingData.put("timestamp", System.currentTimeMillis());

                        db.collection("appRatings")
                                .add(ratingData)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(getActivity(), "Rating submitted successfully", Toast.LENGTH_SHORT).show();
                                    promptForGooglePlayReview();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getActivity(), "Error submitting rating: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error checking existing rating: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void promptForGooglePlayReview() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean hasPromptedReview = preferences.getBoolean("hasPromptedReview", false);

        if (hasPromptedReview) {
            Toast.makeText(getActivity(), "You have already reviewed this app on Play Store.", Toast.LENGTH_SHORT).show();
            return;
        }

        reviewManager.requestReviewFlow()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ReviewInfo reviewInfo = task.getResult();
                        reviewManager.launchReviewFlow(requireActivity(), reviewInfo)
                                .addOnCompleteListener(flowTask -> {
                                    if (flowTask.isSuccessful()) {
                                        // Mark that the review prompt was shown
                                        preferences.edit().putBoolean("hasPromptedReview", true).apply();
                                        Toast.makeText(getActivity(), "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Error showing review dialog.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getActivity(), "Error requesting review flow: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void fetchContentRecyclerViewData() {
        showProgressDialog();

        CollectionReference subItemsRef = db.collection("HomeContents");
        subItemsRef.get().addOnCompleteListener(task -> {
            hideProgressDialog(); // Hide dialog on task completion
            if (task.isSuccessful()) {
                List<HomeContentItem> subContentList = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String subItemImage = document.getString("subContentImage");
                    String subItemTitle = document.getString("subContentTitle");
                    String subItemDesc = document.getString("subContentDesc");
                    String subItemCode = document.getString("subContentId");

                    HomeContentItem subItem = new HomeContentItem(subItemImage, subItemTitle, subItemDesc, subItemCode);
                    subContentList.add(subItem);
                }

                if (getContext() != null) {
                    contentAdapter = new HomeContentAdapter(getContext(), subContentList);
                    rcvBtm.setAdapter(contentAdapter);
                    contentAdapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(e -> hideProgressDialog());
    }

    private void fetchPdfItemRecyclerViewData11th() {
        showProgressDialog();

        CollectionReference itemsRef = db.collection("HomeItemsClass11thPdf");
        itemsRef.get().addOnCompleteListener(task -> {
            hideProgressDialog();
            if (task.isSuccessful()) {
                List<ItemHeader> itemList = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                        String title = document.getString("title");
                        fetchPdfSubItems11th(document.getReference().collection("subItems"), title, itemList);
                }
            }
        }).addOnFailureListener(e -> hideProgressDialog());
    }

    private void fetchPdfSubItems11th(CollectionReference subItemsRef, String title, List<ItemHeader> itemList) {
        subItemsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<HomePdfSubItem> subItemList = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    String imageResource = document.getString("imageResource");
                    String subItemTitle = document.getString("title");
                    String description = document.getString("description");
                    String url = document.getString("url");
                    subItemList.add(new HomePdfSubItem(imageResource, subItemTitle, description, url));
                }
                itemList.add(new ItemHeader(title, subItemList));

                if (itemAdapter == null) {
                    itemAdapter = new ItemHeaderAdapter(getContext(), itemList);
                    rcvTop.setAdapter(itemAdapter);
                } else {
                    itemAdapter.notifyDataSetChanged();
                }
            }
            hideProgressDialog();
        }).addOnFailureListener(e -> hideProgressDialog());
    }

//    private void fetchVideoItemRecyclerViewData11th() {
//        showProgressDialog();
//
//        CollectionReference itemsRef = db.collection("HomeItemsClass11th");
//        itemsRef.get().addOnCompleteListener(task -> {
//            hideProgressDialog(); // Ensure the dialog is hidden on completion
//            if (task.isSuccessful()) {
//                List<ItemHeader> itemList = new ArrayList<>();
//                for (DocumentSnapshot document : task.getResult()) {
//                    if (document.getId().equals("class11thVideo")) {
//                        String title = document.getString("title");
//                        fetchVideoSubItems11th(document.getReference().collection("subItems"), title, itemList);
//                    }
//                }
//            }
//        }).addOnFailureListener(e -> hideProgressDialog());
//    }
//
//    private void fetchVideoSubItems11th(CollectionReference subItemsRef, String title, List<ItemHeader> itemList) {
//        subItemsRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                List<HomeVideoSubItem> subItemList = new ArrayList<>();
//                for (DocumentSnapshot document : task.getResult()) {
//                    String imageResource = document.getString("imageResource");
//                    String subItemTitle = document.getString("title");
//                    String description = document.getString("description");
//                    String url = document.getString("url");
//                    String length = document.getString("length");
//                    subItemList.add(new HomeVideoSubItem(imageResource, subItemTitle, description, url, length));
//                }
//                itemList.add(new ItemHeader(title, subItemList)); // Works with generic List
//                if (itemAdapter == null) {
//                    itemAdapter = new ItemHeaderAdapter(getContext(), itemList);
//                    rcvTop.setAdapter(itemAdapter);
//                } else {
//                    itemAdapter.notifyDataSetChanged();
//                }
//            }
//            hideProgressDialog(); // Ensure the progress dialog is hidden
//        }).addOnFailureListener(e -> hideProgressDialog());
//    }

    private void fetchPdfItemRecyclerViewData12th() {
        showProgressDialog();

        CollectionReference itemsRef = db.collection("HomeItemsClass12thPdf");
        itemsRef.get().addOnCompleteListener(task -> {
            hideProgressDialog(); // Ensure the dialog is hidden on completion
            if (task.isSuccessful()) {
                List<ItemHeader> itemList = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                        String title = document.getString("title");
                        fetchPdfSubItems12th(document.getReference().collection("subItems"), title, itemList);
                }
            }
        }).addOnFailureListener(e -> hideProgressDialog());
    }

    private void fetchPdfSubItems12th(CollectionReference subItemsRef, String title, List<ItemHeader> itemList) {
        subItemsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<HomePdfSubItem> subItemList = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    String imageResource = document.getString("imageResource");
                    String subItemTitle = document.getString("title");
                    String description = document.getString("description");
                    String url = document.getString("url");
                    subItemList.add(new HomePdfSubItem(imageResource, subItemTitle, description, url));
                }
                itemList.add(new ItemHeader(title, subItemList));

                if (itemAdapter == null) {
                    itemAdapter = new ItemHeaderAdapter(getContext(), itemList);
                    rcvTop.setAdapter(itemAdapter);
                } else {
                    itemAdapter.notifyDataSetChanged();
                }
            }
            hideProgressDialog(); // Ensure the progress dialog is hidden
        }).addOnFailureListener(e -> hideProgressDialog());
    }

//    private void fetchVideoItemRecyclerViewData12th() {
//        showProgressDialog();
//
//        CollectionReference itemsRef = db.collection("HomeItemsClass12th");
//        itemsRef.get().addOnCompleteListener(task -> {
//            hideProgressDialog();
//            if (task.isSuccessful()) {
//                List<ItemHeader> itemList = new ArrayList<>();
//                for (DocumentSnapshot document : task.getResult()) {
//                    if (document.getId().equals("class12th")) {
//                        String title = document.getString("title");
//                        fetchVideoSubItems12th(document.getReference().collection("subItems"), title, itemList);
//                    }
//                }
//            }
//        }).addOnFailureListener(e -> hideProgressDialog());
//    }
//
//    private void fetchVideoSubItems12th(CollectionReference subItemsRef, String title, List<ItemHeader> itemList) {
//        subItemsRef.orderBy("timeSpent", Query.Direction.DESCENDING)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        List<HomePdfSubItem> subItemList = new ArrayList<>();
//                        for (DocumentSnapshot document : task.getResult()) {
//                            String imageResource = document.getString("imageResource");
//                            String subItemTitle = document.getString("title");
//                            String description = document.getString("description");
//                            String url = document.getString("url");
//                            subItemList.add(new HomePdfSubItem(imageResource, subItemTitle, description, url));
//                        }
//                        itemList.add(new ItemHeader(title, subItemList));
//
//                        if (itemAdapter == null) {
//                            itemAdapter = new ItemHeaderAdapter(getContext(), itemList);
//                            rcvTop.setAdapter(itemAdapter);
//                        } else {
//                            itemAdapter.notifyDataSetChanged();
//                        }
//                    }
//                    hideProgressDialog(); // Ensure the progress dialog is hidden
//                }).addOnFailureListener(e -> hideProgressDialog());
//    }

//    private void fetchTopPdfRecyclerViewData() {
//        showProgressDialog();
//
//        CollectionReference itemsRef = db.collection("TopPdfWatchTime");
//        itemsRef.get().addOnCompleteListener(task -> {
//            hideProgressDialog(); // Ensure the dialog is hidden on completion
//            if (task.isSuccessful()) {
//                List<ItemHeader> itemList = new ArrayList<>();
//                for (DocumentSnapshot document : task.getResult()) {
//                    String title = document.getString("title");
//                    fetchPdfItems(document.getReference().collection("PdfItems"), title, itemList);
//                }
//            }
//        }).addOnFailureListener(e -> hideProgressDialog());
//    }
//
//    private void fetchPdfItems(CollectionReference subItemsRef, String title, List<ItemHeader> itemList) {
//        subItemsRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                List<HomePdfSubItem> subItemList = new ArrayList<>();
//                for (DocumentSnapshot document : task.getResult()) {
//                    String imageResource = document.getString("img");
//                    String subItemTitle = document.getString("title");
//                    String description = document.getString("subject");
//                    String url = document.getString("url");
//                    subItemList.add(new HomePdfSubItem(imageResource, subItemTitle, description, url));
//                }
//                itemList.add(new ItemHeader(title, subItemList));
//
//                if (itemAdapter == null) {
//                    itemAdapter = new ItemHeaderAdapter(getContext(), itemList);
//                    rcvTop.setAdapter(itemAdapter);
//                } else {
//                    itemAdapter.notifyDataSetChanged();
//                }
//            }
//            hideProgressDialog(); // Hide dialog even if sub-items fetch completes
//        }).addOnFailureListener(e -> hideProgressDialog());
//    }

    private void fetchSlidingImagesFromFirestore() {
        showProgressDialog();

        db.collection("SLIDINGIMAGES").get()
                .addOnCompleteListener(task -> {
                    hideProgressDialog(); // Ensure dialog is hidden on completion
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            String imageUrl = document.getString("url");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                slideModels.add(new SlideModel(imageUrl, ScaleTypes.CENTER_CROP));
                            }
                        }
                        if (!slideModels.isEmpty()) {
                            imageSlider.setImageList(slideModels, ScaleTypes.CENTER_CROP);
                        }
                    }
                }).addOnFailureListener(e -> hideProgressDialog());
    }

    private void showProgressDialog() {
        if (getActivity() == null) {
            // Log or handle the null activity case appropriately
            return;
        }

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    private void startHintCycling() {
        hintUpdater = new Runnable() {
            @Override
            public void run() {
                searchBar.setHint(hints[currentHintIndex]);
                currentHintIndex = (currentHintIndex + 1) % hints.length;
                handler.postDelayed(this, 3000);
            }
        };
        handler.post(hintUpdater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacks(hintUpdater);
        }
        if (userListener != null) {
            userListener.remove();
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            hideProgressDialog();
            progressDialog = null;
        }
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                fetchUserData(currentUser.getUid());
//                fetchItemRecyclerViewData();
                handleClassSelection();
                fetchContentRecyclerViewData();
            }
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void fetchUserData(String userId) {
        userListener = db.collection("users").document(userId).addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                updateUserUI(snapshot);
            }
        });
    }

    private void updateUserUI(DocumentSnapshot snapshot) {
        String userNameStr = snapshot.getString("name");
        String profileImageUrl = snapshot.getString("photoUrl");

        if (userNameStr != null) {
            userName.setText(userNameStr);
            userName.setSelected(true);
        }

        if (isAdded() && profileImageUrl != null) {
            Glide.with(this)
                    .load(profileImageUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            hideProgressDialog();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            hideProgressDialog();
                            return false;
                        }
                    })
                    .into(userImg);
        }
    }

    private void setupNavigationDrawer(View view) {
        DrawerLayout drawerLayout = view.findViewById(R.id.drawerLayout);
        NavigationView navigationView = view.findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);

        navVideoWatchTime = headerView.findViewById(R.id.videoWatch);
        navVideoWatchTime.setSelected(true);
        navPdfWatchTime = headerView.findViewById(R.id.pdfWatch);
        navPdfWatchTime.setSelected(true);
        navMockSpendTime = headerView.findViewById(R.id.averageUse);
        navMockSpendTime.setSelected(true);
        navLoginSince = headerView.findViewById(R.id.loginSince);
        navLoginSince.setSelected(true);

        navHeaderUserName = headerView.findViewById(R.id.getUserNameNav);
        navHeaderEmail = headerView.findViewById(R.id.getEmailNav);
        navHeaderImg = headerView.findViewById(R.id.getImageNav);
        navHeaderEditBtn = headerView.findViewById(R.id.profileImgUpdateBtn);
        switchCompat = headerView.findViewById(R.id.switch_mode);
        navRadioGroup = headerView.findViewById(R.id.radioGroupClass);
        setupClassSelection(navRadioGroup);
        setupTheme();
        TextView developedByTextView = navigationView.findViewById(R.id.tv_developed_by);
        addTextWithImage(developedByTextView);
        developedByTextView.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://www.instagram.com/sansas.io/"));
            startActivity(intent);
        });

        navHeaderEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomBottomSheet bottomSheet = new CustomBottomSheet();
                bottomSheet.show(getParentFragmentManager(), "CustomBottomSheet");
            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            navHeaderUserName.setText(snapshot.getString("name"));
                            navHeaderEmail.setText(snapshot.getString("email"));
                            String imageUrl = snapshot.getString("photoUrl");
                            String loginSinceDate = snapshot.getString("loginSince");

                            if (loginSinceDate != null) {
                                navLoginSince.setText(String.format("Login Since: %s", loginSinceDate));
                            } else {
                                navLoginSince.setText("Login Since: Unknown");
                            }

                            if (isAdded() && imageUrl != null) {
                                Glide.with(this)
                                        .load(imageUrl)
                                        .placeholder(R.mipmap.ic_launcher)
                                        .error(R.mipmap.ic_launcher)
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                hideProgressDialog();
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                hideProgressDialog();
                                                return false;
                                            }
                                        })
                                        .into(navHeaderImg);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                    });

            db.collection("users").document(userId)
                    .collection("pdfWatchTime")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        long totalPdfTime = 0;

                        for (DocumentSnapshot document : querySnapshot) {
                            Long timeSpent = document.getLong("timeSpent");
                            if (timeSpent != null) {
                                totalPdfTime += timeSpent;
                            }
                        }

                        long hours = totalPdfTime / 3600000;
                        long minutes = (totalPdfTime % 3600000) / 60000;
                        long seconds = (totalPdfTime % 60000) / 1000;

                        navPdfWatchTime.setText(String.format("PDF Time: \n%dh:%dm:%ds", hours, minutes, seconds));
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Failed to fetch PDF watch time", Toast.LENGTH_SHORT).show();
                    });

            db.collection("users").document(userId)
                    .collection("videoWatchTime")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        long totalVideoTime = 0;

                        for (DocumentSnapshot document : querySnapshot) {
                            Long timeSpent = document.getLong("timeSpent");
                            if (timeSpent != null) {
                                totalVideoTime += timeSpent;
                            }
                        }

                        long hours = totalVideoTime / 3600000;
                        long minutes = (totalVideoTime % 3600000) / 60000;
                        long seconds = (totalVideoTime % 60000) / 1000;

                        navVideoWatchTime.setText(String.format("Video Time: \n%dh:%dm:%ds", hours, minutes, seconds));
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Failed to fetch PDF watch time", Toast.LENGTH_SHORT).show();
                    });

            db.collection("users").document(userId)
                    .collection("mockSpendTime")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        long totalPdfTime = 0;

                        for (DocumentSnapshot document : querySnapshot) {
                            Long timeSpent = document.getLong("timeSpent");
                            if (timeSpent != null) {
                                totalPdfTime += timeSpent;
                            }
                        }

                        long hours = totalPdfTime / 3600000; // 1 hour = 3600000 ms
                        long minutes = (totalPdfTime % 3600000) / 60000;
                        long seconds = (totalPdfTime % 60000) / 1000;

                        navMockSpendTime.setText(String.format("Mock Time: \n%dh:%dm:%ds", hours, minutes, seconds));
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Failed to fetch PDF watch time", Toast.LENGTH_SHORT).show();
                    });
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_your_exam) {
                    openYourExam();
                    return true;
                } else if (itemId == R.id.nav_quick_learning) {
                    openQuickLearning();
                    return true;
                } else if (itemId == R.id.nav_my_downloads) {
                    openMyDownloads();
                    return true;
                } else if (itemId == R.id.nav_scholarship) {
                    openScholarship();
                    return true;
                } else if (itemId == R.id.nav_out_result) {
                    openOutResult();
                    return true;
                } else if (itemId == R.id.nav_about_us) {
                    openAboutUs();
                    return true;
                } else if (itemId == R.id.nav_vt_center) {
                    openVTCenter();
                    return true;
                } else if (itemId == R.id.nav_logout) {
                    logout();
                    return true;
                } else if (itemId == R.id.nav_deleteAccount) {
                    deleteAccount();
                    return true;
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

//    private void updateClassSelectionInFirestore(boolean isClass11th, boolean isClass12th) {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String uid = currentUser.getUid();
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//            Map<String, Object> updates = new HashMap<>();
//            updates.put("class11th", isClass11th);
//            updates.put("class12th", isClass12th);
//
//            db.collection("users").document(uid)
//                    .update(updates)
//                    .addOnSuccessListener(aVoid -> {
//                        if (isClass11th) {
//                            navRadioGroup.check(R.id.radioButtonClass11);
//                            Snackbar.make(requireView(), "Selected Class 11th!", Snackbar.LENGTH_SHORT).show();
//                        } else if (isClass12th) {
//                            navRadioGroup.check(R.id.radioButtonClass12);
//                            Snackbar.make(requireView(), "Selected Class 12th!", Snackbar.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(getContext(), "Failed to update class selection: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    });
//        } else {
//            Toast.makeText(getContext(), "Please log in to update class selection.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    private void fetchUserClassSelection() {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String uid = currentUser.getUid();
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//            db.collection("users").document(uid)
//                    .get()
//                    .addOnSuccessListener(documentSnapshot -> {
//                        if (documentSnapshot.exists()) {
//                            Boolean class11th = documentSnapshot.getBoolean("class11th");
//                            Boolean class12th = documentSnapshot.getBoolean("class12th");
//
//                            if (class11th != null && class11th) {
//                                navRadioGroup.check(R.id.radioButtonClass11);
//                            } else if (class12th != null && class12th) {
//                                navRadioGroup.check(R.id.radioButtonClass12);
//                            } else {
//                                navRadioGroup.clearCheck();
//                            }
//                        } else {
//                            Snackbar.make(requireView(), "User data not found.", Snackbar.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                        Snackbar.make(requireView(), "Failed to fetch class selection: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
//                    });
//        } else {
//            Toast.makeText(getContext(), "Please log in to fetch class selection.", Toast.LENGTH_SHORT).show();
//        }
//    }

//    private void restartHomeFragment() {
//        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.fragment_container, new HomeFragment());
//        fragmentTransaction.setReorderingAllowed(true);
//        fragmentTransaction.commit();
//    }

    private void restartHomeFragment(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START); // ✅ Close the drawer
            drawerLayout.postDelayed(() -> restartFragment(), 300); // ✅ Delay restart for smooth transition
        } else {
            restartFragment(); // ✅ If already closed, restart immediately
        }
    }

    // ✅ Extracted method for fragment restart
    private void restartFragment() {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new HomeFragment());
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commit();
    }



//    private void setupClassSelection(RadioGroup navRadioGroup) {
//        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CLASS_SELECTION", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
//                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
//
//        if (userId != null) {
//            firestore.collection("users").document(userId).get()
//                    .addOnSuccessListener(documentSnapshot -> {
//                        if (documentSnapshot.exists()) {
//                            Boolean isClass11Selected = documentSnapshot.getBoolean("class11th");
//                            Boolean isClass12Selected = documentSnapshot.getBoolean("class12th");
//
//                            if (isClass11Selected != null && isClass11Selected) {
//                                navRadioGroup.check(R.id.radioButtonClass11);
//                            } else if (isClass12Selected != null && isClass12Selected) {
//                                navRadioGroup.check(R.id.radioButtonClass12);
//                            } else {
//                                navRadioGroup.clearCheck();
//                            }
//
//                            String selectedClass = isClass11Selected != null && isClass11Selected ?
//                                    "Class 11th" : "Class 12th";
//                            editor.putString("selectedClass", selectedClass);
//                            editor.apply();
//                        } else {
//                            navRadioGroup.check(R.id.radioButtonClass11);
//                            editor.putString("selectedClass", "Class 11th");
//                            editor.apply();
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                        String fallbackClass = sharedPreferences.getString("selectedClass", "Class 11th");
//                        if ("Class 11th".equals(fallbackClass)) {
//                            navRadioGroup.check(R.id.radioButtonClass11);
//                        } else {
//                            navRadioGroup.check(R.id.radioButtonClass12);
//                        }
//                    });
//
//            navRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
//                String selectedClass;
//                if (checkedId == R.id.radioButtonClass11) {
//                    updateClassSelectionInFirestore(true, false);
//                    selectedClass = "Class 11th";
//                } else {
//                    updateClassSelectionInFirestore(false, true);
//                    selectedClass = "Class 12th";
//                }
//
//                firestore.collection("users").document(userId).update("selectedClass", selectedClass)
//                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save selection.", Toast.LENGTH_SHORT).show());
//
//                editor.putString("selectedClass", selectedClass);
//                editor.apply();
//            });
//        }
//    }

    private void setupClassSelection(RadioGroup navRadioGroup) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CLASS_SELECTION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId != null) {
            firestore.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Boolean isClass11Selected = documentSnapshot.getBoolean("class11th");
                            Boolean isClass12Selected = documentSnapshot.getBoolean("class12th");
                            String selectedClass = documentSnapshot.getString("selectedClass");

                            if (selectedClass == null || (!Boolean.TRUE.equals(isClass11Selected) && !Boolean.TRUE.equals(isClass12Selected))) {
                                // No class selected, clear the selection
                                navRadioGroup.clearCheck();
                                editor.putString("selectedClass", null);
                            } else {
                                // Set the class selection if available
                                navRadioGroup.check(isClass11Selected ? R.id.radioButtonClass11 : R.id.radioButtonClass12);
                                editor.putString("selectedClass", selectedClass);
                            }
                            editor.apply();
                        } else {
                            // Document does not exist, clear the selection
                            navRadioGroup.clearCheck();
                            editor.putString("selectedClass", null);
                            editor.apply();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Error fetching data, clear the selection
                        navRadioGroup.clearCheck();
                        editor.putString("selectedClass", null);
                        editor.apply();
                    });

            navRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                String selectedClass = null;
                boolean isClass11th = false, isClass12th = false;

                if (checkedId == R.id.radioButtonClass11) {
                    selectedClass = "Class 11th";
                    isClass11th = true;
                } else if (checkedId == R.id.radioButtonClass12) {
                    selectedClass = "Class 12th";
                    isClass12th = true;
                }

                updateClassSelectionInFirestore(isClass11th, isClass12th, selectedClass);

                firestore.collection("users").document(userId).update("selectedClass", selectedClass)
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save selection.", Toast.LENGTH_SHORT).show());

                editor.putString("selectedClass", selectedClass);
                editor.apply();
            });
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
                        // Firestore updated successfully
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to update class selection in Firestore.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "Please log in to update class selection.", Toast.LENGTH_SHORT).show();
        }
    }



//    private void updateClassSelectionInFirestore(boolean isClass11th, boolean isClass12th, String selectedClass) {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String uid = currentUser.getUid();
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//            Map<String, Object> updates = new HashMap<>();
//            updates.put("class11th", isClass11th);
//            updates.put("class12th", isClass12th);
//            updates.put("selectedClass", selectedClass);
//
//            db.collection("users").document(uid)
//                    .update(updates)
//                    .addOnSuccessListener(aVoid -> {
//                        // Use SharedPreferences in Fragment
//                        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CLASS_SELECTION", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString("selectedClass", selectedClass);
//                        editor.apply();
//
//                        Toast.makeText(requireContext(), "Class selection updated to " + selectedClass, Toast.LENGTH_SHORT).show();
//                    })
//                    .addOnFailureListener(e ->
//                            Toast.makeText(requireContext(), "Failed to update class selection. Try again.", Toast.LENGTH_SHORT).show()
//                    );
//        } else {
//            Toast.makeText(requireContext(), "Please log in to update class selection.", Toast.LENGTH_SHORT).show();
//        }
//    }

//    private void updateClassSelectionInFirestore(boolean isClass11th, boolean isClass12th, String selectedClass) {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String uid = currentUser.getUid();
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//            Map<String, Object> updates = new HashMap<>();
//            updates.put("class11th", isClass11th);
//            updates.put("class12th", isClass12th);
//            updates.put("selectedClass", selectedClass);
//
//            db.collection("users").document(uid)
//                    .update(updates)
//                    .addOnSuccessListener(aVoid -> {
//                        // Firestore updated successfully
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(getContext(), "Failed to update class selection in Firestore.", Toast.LENGTH_SHORT).show();
//                    });
//        } else {
//            Toast.makeText(getContext(), "Please log in to update class selection.", Toast.LENGTH_SHORT).show();
//        }
//    }


//    private void updateClassSelectionInFirestore(boolean isClass11th, boolean isClass12th) {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String uid = currentUser.getUid();
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//            Map<String, Object> updates = new HashMap<>();
//            updates.put("class11th", isClass11th);
//            updates.put("class12th", isClass12th);
//
//            db.collection("users").document(uid)
//                    .update(updates)
//                    .addOnSuccessListener(aVoid -> {
//                        // Firestore updated successfully
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(getContext(), "Failed to update class selection in Firestore.", Toast.LENGTH_SHORT).show();
//                    });
//        } else {
//            Toast.makeText(getContext(), "Please log in to update class selection.", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void setupTheme() {
        sharedPreferences = getActivity().getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("nightMode", false);

        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            switchCompat.setChecked(true);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            switchCompat.setChecked(false);
        }

        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                animateSwitch();
                if (nightMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    nightMode = false;
                    switchCompat.setChecked(false);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    nightMode = true;
                    switchCompat.setChecked(true);
                }

                animateThemeChange();
                editor = sharedPreferences.edit();
                editor.putBoolean("nightMode", nightMode);
                editor.apply();
            }
        });
    }

    private void animateSwitch() {
        float endTranslationX = switchCompat.isChecked() ? 100f : -100f;

        ObjectAnimator switchAnimator = ObjectAnimator.ofFloat(switchCompat, "translationX", 0f, endTranslationX);
        switchAnimator.setDuration(300);
        switchAnimator.setInterpolator(new DecelerateInterpolator());
        switchAnimator.start();
    }

    private void animateThemeChange() {
        final View decorView = getActivity().getWindow().getDecorView();
        final ViewGroup rootView = decorView.findViewById(android.R.id.content);

        rootView.animate().alpha(0f).setDuration(300).withEndAction(new Runnable() {
            @Override
            public void run() {
                rootView.animate().alpha(1f).setDuration(300).start();
            }
        }).start();
    }

    private void addTextWithImage(TextView textView) {
        String text = "Developed by sansas.io | ";
        SpannableString spannable = new SpannableString(text + " ");
        Drawable drawable = getResources().getDrawable(R.drawable.ig);
        int drawableSize = (int) 20;
        drawable.setBounds(0, 0, drawableSize, drawableSize);
        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        spannable.setSpan(imageSpan, text.length(), text.length() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
    }


    private void openYourExam() {
        startActivity(new Intent(getActivity(), YourExamActivity.class));
    }

    private void openQuickLearning() {
        startActivity(new Intent(getActivity(), QuickLearningActivity.class));
    }

    private void openVTCenter() {
        startActivity(new Intent(getActivity(), CenterActivity.class));
    }

    private void openScholarship() {
        startActivity(new Intent(getActivity(), ScholarshipActivity.class));
    }

    private void openOutResult() {
        startActivity(new Intent(getActivity(), OurResultActivity.class));
    }

    private void openMyDownloads() {
        startActivity(new Intent(getActivity(), BookmarksActivity.class));
    }

    private void openAboutUs() {
        startActivity(new Intent(getActivity(), AboutActivity.class));
    }

    private void logout() {
        if (!isAdded()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");

        builder.setPositiveButton("Logout", (dialog, which) -> {
            // Clear login status from SharedPreferences
            SharedPreferences preferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            // Sign out from Firebase
            mAuth.signOut();

            // Sign out from Google
            GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
                    .addOnCompleteListener(requireActivity(), task -> {
                        Toast.makeText(requireContext(), "Successfully logged out", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        requireActivity().finish(); // Close the current activity
                    });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void deleteAccount() {
        if (!isAdded()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();

                    if (user == null) {
                        showToast("No user signed in");
                        return;
                    }

                    ProgressDialog progressDialog = new ProgressDialog(requireContext());
                    progressDialog.setMessage("Deleting account...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    if (isGoogleAccountLinked(user)) {
                        deleteGoogleAccountData(user, db, progressDialog);
                    } else {
                        deleteFirestoreDataAndAccount(user, db, progressDialog);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private boolean isGoogleAccountLinked(FirebaseUser user) {
        return user.getProviderData().stream().anyMatch(p -> p.getProviderId().equals(GoogleAuthProvider.PROVIDER_ID));
    }

    private void deleteGoogleAccountData(FirebaseUser user, FirebaseFirestore db, ProgressDialog progressDialog) {
        String userId = user.getUid();

        // Delete user data from Firestore
        db.collection("users").document(userId).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("DeleteAccount", "User data deleted from Firestore.");
                    revokeGoogleAccess(user, progressDialog);
                })
                .addOnFailureListener(e -> {
                    Log.e("DeleteAccount", "Error deleting user data: " + e.getMessage());
                    showToast("Error deleting user data: " + e.getMessage());
                });
    }

    private void revokeGoogleAccess(FirebaseUser user, ProgressDialog progressDialog) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
        googleSignInClient.revokeAccess().addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                Log.d("DeleteAccount", "Google access revoked.");
                deleteFirebaseUser(user, progressDialog);
            } else {
                Log.e("DeleteAccount", "Error revoking Google access: " + task.getException().getMessage());
                showToast("Error revoking access: " + task.getException().getMessage());
            }
        });
    }

    private void deleteFirestoreDataAndAccount(FirebaseUser user, FirebaseFirestore db, ProgressDialog progressDialog) {
        String userId = user.getUid();

        // Delete user data from Firestore
        db.collection("users").document(userId).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("DeleteAccount", "User data deleted from Firestore.");
                    deleteFirebaseUser(user, progressDialog);
                })
                .addOnFailureListener(e -> {
                    Log.e("DeleteAccount", "Error deleting user data: " + e.getMessage());
                    showToast("Error deleting Firestore data: " + e.getMessage());
                });
    }

    private void deleteFirebaseUser(FirebaseUser user, ProgressDialog progressDialog) {
        user.delete().addOnCompleteListener(deleteTask -> {
            if (deleteTask.isSuccessful()) {

                SharedPreferences preferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();

                showToast("Account deleted successfully.");
                navigateToLogin();
            } else {
                Log.e("DeleteAccount", "Error deleting Firebase user: " + deleteTask.getException().getMessage());
                showToast("Error deleting account: " + deleteTask.getException().getMessage());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }




    private void setupGreetingMessage() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (hourOfDay >= 0 && hourOfDay < 12) {
            greetText.setText("Good Morning ⛅");
        } else if (hourOfDay >= 12 && hourOfDay < 17) {
            greetText.setText("Good Afternoon ☀");
        } else {
            greetText.setText("Good Evening \uD83C\uDF1A");
        }
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);


            toolbar.setNavigationIcon(R.drawable.gg_details_more);
            toolbar.setNavigationOnClickListener(v -> {

                DrawerLayout drawerLayout = view.findViewById(R.id.drawerLayout);
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });

            DrawerLayout drawerLayout = view.findViewById(R.id.drawerLayout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    getActivity(), drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }
    }

    private void animateViews(View view) {
        // Example: Fade In Animation
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(500); // Duration in milliseconds
        view.startAnimation(fadeIn);

        // Or apply other animations like scaling or rotation
        view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(500).start();
    }


    @Override
    public void onStart() {
        super.onStart();
        animateViews(greetText);
        animateViews(userName);
        animateViews(userImg);
    }
}
