package com.sansan.vikashtutorial.Library;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.search.SearchBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sansan.vikashtutorial.Pdf.PdfAdapter;
import com.sansan.vikashtutorial.Pdf.PdfItem;
import com.sansan.vikashtutorial.R;
import com.sansan.vikashtutorial.Search.SearchPdfActivity;

import java.util.ArrayList;
import java.util.List;

public class ZoologyLibraryFragment extends Fragment {

    private ImageView noListImage;
    private RecyclerView recyclerView;
    private PdfAdapter adapter;
    private List<PdfItem> pdfItemList;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firestore;
    private SearchBar searchBar;
    public ZoologyLibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_zoology_library, container, false);

        searchBar = view.findViewById(R.id.searchBar);
        noListImage = view.findViewById(R.id.noListMessage);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        pdfItemList = new ArrayList<>();
        adapter = new PdfAdapter(getContext(), pdfItemList);
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        fetchDataFromFireStore();

        searchBar.setOnClickListener(v -> startActivity(new Intent(getActivity(), SearchPdfActivity.class)));

        return view;
    }

    private void fetchDataFromFireStore() {
        progressDialog.show();
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(getContext(), "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean isClass11thSelected = documentSnapshot.getBoolean("class11th");
                        Boolean isClass12thSelected = documentSnapshot.getBoolean("class12th");

                        String collectionName = null;

                        if (Boolean.TRUE.equals(isClass11thSelected) && Boolean.FALSE.equals(isClass12thSelected)) {
                            collectionName = "11thZoologyLibrary";
                        } else if (Boolean.TRUE.equals(isClass12thSelected) && Boolean.FALSE.equals(isClass11thSelected)) {
                            collectionName = "12thZoologyLibrary";
                        }

                        if (collectionName != null) {
                            fetchPdfData(collectionName);
                        } else {
                            Toast.makeText(getContext(), "Invalid class selection.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void fetchPdfData(String collectionName) {
        CollectionReference pdfCollectionRef = FirebaseFirestore.getInstance().collection(collectionName);
        progressDialog.show();

        pdfCollectionRef.addSnapshotListener((value, error) -> {
            progressDialog.dismiss();

            if (error != null) {
                Toast.makeText(getContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            pdfItemList.clear();

            if (value != null) {
                for (QueryDocumentSnapshot document : value) {
                    PdfItem pdfItem = document.toObject(PdfItem.class);
                    pdfItemList.add(pdfItem);
                }
                adapter.notifyDataSetChanged();

                if (pdfItemList.isEmpty()) {
                    noListImage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    noListImage.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}