package com.sansan.vikashtutorial;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sansan.vikashtutorial.Adapter.DropDownAdapter;
import com.sansan.vikashtutorial.Item.DropDownItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NEETFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<DropDownItem> mList;
    private DropDownAdapter adapter;
    private FirebaseFirestore firestore;
    private ProgressDialog progressDialog;
    private ImageView noListImage;

    public NEETFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_n_e_e_t, container, false);

        noListImage = view.findViewById(R.id.noListImage);
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Loading...");

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        mList = new ArrayList<>();
        adapter = new DropDownAdapter(requireContext(), mList);
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        fetchDataFromFireStore();

        return view;
    }

    private void fetchDataFromFireStore() {
        progressDialog.show();
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (userId == null) {
            progressDialog.dismiss();
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
                            collectionName = "11thNEETLibrary";
                        } else if (Boolean.TRUE.equals(isClass12thSelected) && Boolean.FALSE.equals(isClass11thSelected)) {
                            collectionName = "12thNEETLibrary";
                        }

                        if (collectionName != null) {
                            fetchSemesters(collectionName);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Invalid class selection.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchSemesters(String collectionName) {
        progressDialog.show();
        firestore.collection(collectionName)
                .get()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        mList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String semesterName = document.getString("listName");
                            String examImg = document.getString("img");
                            List<Map<String, String>> subjects = (List<Map<String, String>>) document.get("items");

                            List<String> subjectNames = new ArrayList<>();
                            List<String> subjectUrls = new ArrayList<>();

                            if (subjects != null) {
                                for (Map<String, String> subject : subjects) {
                                    subjectNames.add(subject.get("name"));
                                    subjectUrls.add(subject.get("url"));
                                }
                            }

                            mList.add(new DropDownItem(subjectNames, subjectUrls, semesterName, examImg, false));
                        }

                        adapter.notifyDataSetChanged();

                        if (mList.isEmpty()) {
                            noListImage.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            noListImage.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.e("Firestore", "Error fetching documents", task.getException());
                        noListImage.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                });
    }
}