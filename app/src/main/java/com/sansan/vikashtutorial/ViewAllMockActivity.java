package com.sansan.vikashtutorial;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sansan.vikashtutorial.Mock.MockAdapter;
import com.sansan.vikashtutorial.Mock.MockItem;
import com.sansan.vikashtutorial.Search.SearchMockActivity;

import java.util.ArrayList;
import java.util.List;

public class ViewAllMockActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private List<MockItem> itemList = new ArrayList<>();
    private ViewAllMockAdapter adapter;
    private ImageButton searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_mock);

        Animation top_to_btm = AnimationUtils.loadAnimation(this, R.anim.top_to_btm);
        View view = findViewById(R.id.topCircle);
        view.setAnimation(top_to_btm);

        SharedPreferences sharedPreferences = getSharedPreferences("MockPrefs", MODE_PRIVATE);
        String selectedCategory = sharedPreferences.getString("selectedCategory", null);
        String selectedHeader = sharedPreferences.getString("selectedHeader", null);

        searchBar = findViewById(R.id.searchBar);
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("MockPrefs", MODE_PRIVATE);
                String selectedCategory = sharedPreferences.getString("selectedCategory", null);
                String selectedHeader = sharedPreferences.getString("selectedHeader", null);

                SharedPreferences preferences = getSharedPreferences("MockSearchPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("selectedCategory", selectedCategory);
                editor.putString("selectedHeader", selectedHeader);
                editor.apply();

                Intent intent = new Intent(ViewAllMockActivity.this, SearchMockActivity.class);
                startActivity(intent);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.viewAllRecyclerview);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        adapter = new ViewAllMockAdapter(this, itemList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        if (selectedCategory != null && selectedHeader != null) {
            fetchDataBasedOnCategoryAndHeader(selectedCategory, selectedHeader);
        } else {
            Toast.makeText(this, "No category or header selected.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchDataBasedOnCategoryAndHeader(String category, String header) {
        DatabaseReference mockRef = FirebaseDatabase.getInstance().getReference(category + "/" + header);

        mockRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                itemList.clear(); // Clear the previous list to avoid duplicates
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MockItem mockItem = snapshot.getValue(MockItem.class);
                    if (mockItem != null) {
                        itemList.add(mockItem);
                    }
                }
                adapter.notifyDataSetChanged();
                updateToolbarTitle(header);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(ViewAllMockActivity.this, "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateToolbarTitle(String header) {
        int itemCount = itemList.size();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(header + " (" + itemCount + " Quiz)");
        }
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