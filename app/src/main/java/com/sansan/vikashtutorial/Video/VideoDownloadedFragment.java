package com.sansan.vikashtutorial.Video;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sansan.vikashtutorial.Activity.LoginActivity;
import com.sansan.vikashtutorial.R;

import java.util.ArrayList;
import java.util.List;

public class VideoDownloadedFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<VideoBookmarkItem> bookmarkList;
    private VideoBookmarkAdapter bookmarkAdapter;
    private ProgressDialog progressDialog;
    private ImageView noBookmarksMessage;
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;

    public VideoDownloadedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_downloaded, container, false);

        noBookmarksMessage = view.findViewById(R.id.noBookmarksMessage);
        recyclerView = view.findViewById(R.id.recyclerView);
        bookmarkList = new ArrayList<>();
        bookmarkAdapter = new VideoBookmarkAdapter(getContext(), bookmarkList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(bookmarkAdapter);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        fetchBookmarks();

        return view;
    }

    private void fetchBookmarks() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
            return;
        }

        String userId = currentUser.getUid();
        db = FirebaseFirestore.getInstance();

        CollectionReference bookmarkRef = db.collection("users").document(userId).collection("VideoBookmarks");

        listenerRegistration = bookmarkRef.addSnapshotListener((QuerySnapshot snapshot, FirebaseFirestoreException e) -> {
            if (e != null) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Failed to load bookmarks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            bookmarkList.clear();
            if (snapshot != null) {
                for (QueryDocumentSnapshot document : snapshot) {
                    VideoBookmarkItem bookmark = document.toObject(VideoBookmarkItem.class);
                    if (bookmark != null) {
                        bookmarkList.add(bookmark);
                    }
                }
            }
            bookmarkAdapter.notifyDataSetChanged();

            if (bookmarkList.isEmpty()) {
                noBookmarksMessage.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                noBookmarksMessage.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}