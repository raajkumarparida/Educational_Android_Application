package com.sansan.vikashtutorial.Mock;

import android.app.ProgressDialog;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sansan.vikashtutorial.R;

import java.util.ArrayList;
import java.util.List;

public class MockDownloadedFragment extends Fragment {
    private RecyclerView recyclerView;
    private MockBookmarkAdapter mockBookmarkAdapter;
    private FirebaseFirestore db;
    private List<MockBookmarkItem> mockBookmarkItemList;
    private ProgressDialog progressDialog;
    private ImageView noBookmarksMessage;

    public MockDownloadedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mock_downloaded, container, false);

        noBookmarksMessage = view.findViewById(R.id.noBookmarksMessage);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        recyclerView = view.findViewById(R.id.recyclerView);
        db = FirebaseFirestore.getInstance();
        mockBookmarkItemList = new ArrayList<>();
        mockBookmarkAdapter = new MockBookmarkAdapter(getContext(), mockBookmarkItemList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mockBookmarkAdapter);

        fetchQuizData();

        return view;
    }

    private void fetchQuizData() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();
        CollectionReference quizRef = db.collection("users").document(uid).collection("QuizBookmarked");

        progressDialog.show();

        quizRef.addSnapshotListener((value, error) -> {
            progressDialog.dismiss();;

            if (error != null) {
                Toast.makeText(getActivity(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            mockBookmarkItemList.clear();

            if (value != null) {
                for (QueryDocumentSnapshot document : value) {
                    MockBookmarkItem quiz = document.toObject(MockBookmarkItem.class);
                    quiz.setDocumentId(document.getId());
                    mockBookmarkItemList.add(quiz);
                }

                mockBookmarkAdapter.notifyDataSetChanged();

                if (mockBookmarkItemList.isEmpty()) {
                    noBookmarksMessage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    noBookmarksMessage.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}