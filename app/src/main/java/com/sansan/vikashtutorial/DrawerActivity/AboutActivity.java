package com.sansan.vikashtutorial.DrawerActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sansan.vikashtutorial.Adapter.FacultyImageAdapter;
import com.sansan.vikashtutorial.Item.FacultyItem;
import com.sansan.vikashtutorial.R;

import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private List<FacultyItem> facultyItemList;
    private FacultyImageAdapter adapter;
    private ProgressDialog progressDialog;
    private ImageButton ig, fb, yt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ig = findViewById(R.id.instagramLink);
        ig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.instagram.com"));
                startActivity(intent);
            }
        });

        fb = findViewById(R.id.facebookLink);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.facebook.com"));
                startActivity(intent);
            }
        });

        yt = findViewById(R.id.youtubeLink);
        yt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://youtube.com"));
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        db = FirebaseFirestore.getInstance();
        facultyItemList = new ArrayList<>();

        adapter = new FacultyImageAdapter(this, facultyItemList);
        recyclerView.setAdapter(adapter);

        fetchDataFromFireStore();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    private void fetchDataFromFireStore() {
        CollectionReference CollectionRef = db.collection("FacultyImages");
        progressDialog.show();
        CollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                progressDialog.dismiss();

                if (error != null) {
                    Toast.makeText(AboutActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                facultyItemList.clear();

                if (value != null) {
                    for (QueryDocumentSnapshot document : value) {
                        FacultyItem facultyItem = document.toObject(FacultyItem.class);
                        facultyItemList.add(facultyItem);
                    }
                    adapter.notifyDataSetChanged();

                }
            }
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
