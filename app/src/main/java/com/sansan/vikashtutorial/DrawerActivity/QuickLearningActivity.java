package com.sansan.vikashtutorial.DrawerActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sansan.vikashtutorial.Adapter.DropDownAdapter;
import com.sansan.vikashtutorial.Item.DropDownItem;
import com.sansan.vikashtutorial.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuickLearningActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<DropDownItem> mList;
    private DropDownAdapter adapter;
    private FirebaseFirestore firestore;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_learning);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mList = new ArrayList<>();
        adapter = new DropDownAdapter(this, mList);
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        fetchSemesters();
    }

    private void fetchSemesters() {
        progressDialog.show();
        firestore.collection("QuickLearning")
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
                    } else {
                        Log.e("Firestore", "Error fetching documents", task.getException());
                    }
                });
    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}