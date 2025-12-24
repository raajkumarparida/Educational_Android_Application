package com.sansan.vikashtutorial.Pdf;

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

public class PdfDownloadedFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<PdfBookmarkItem> bookmarkList;
    private PdfBookmarkAdapter bookmarkAdapter;
    private ProgressDialog progressDialog;
    private ImageView noBookmarksMessage;
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;
    public PdfDownloadedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pdf_downloaded, container, false);

        noBookmarksMessage = view.findViewById(R.id.noBookmarksMessage);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        recyclerView = view.findViewById(R.id.recyclerView);
        bookmarkList = new ArrayList<>();
        bookmarkAdapter = new PdfBookmarkAdapter(getContext(), bookmarkList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(bookmarkAdapter);

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

        CollectionReference bookmarkRef = db.collection("users").document(userId).collection("NoteBookmarks");

        listenerRegistration = bookmarkRef.addSnapshotListener((QuerySnapshot snapshot, FirebaseFirestoreException e) -> {
            if (e != null) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Failed to load bookmarks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            bookmarkList.clear();
            if (snapshot != null) {
                for (QueryDocumentSnapshot document : snapshot) {
                    PdfBookmarkItem bookmark = document.toObject(PdfBookmarkItem.class);
                    if (bookmark != null) {
                        bookmarkList.add(bookmark);
                    }
                }
            }
            bookmarkAdapter.notifyDataSetChanged();

            if (bookmarkList.isEmpty()) {
                noBookmarksMessage.setVisibility(View.VISIBLE); // Show message
                recyclerView.setVisibility(View.GONE); // Hide RecyclerView
            } else {
                noBookmarksMessage.setVisibility(View.GONE); // Hide message
                recyclerView.setVisibility(View.VISIBLE); // Show RecyclerView
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            // Optionally redirect to login activity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish(); // Optionally close the current activity
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