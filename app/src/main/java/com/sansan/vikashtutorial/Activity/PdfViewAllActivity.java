package com.sansan.vikashtutorial.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.search.SearchBar;
import com.sansan.vikashtutorial.Home.HomePdfSubItem;
import com.sansan.vikashtutorial.R;
import com.sansan.vikashtutorial.Search.SearchPdfActivity;
import com.sansan.vikashtutorial.Adapter.ViewSubItemAdapter;

import java.util.ArrayList;

public class PdfViewAllActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ViewSubItemAdapter subItemAdapter;
    private ArrayList<HomePdfSubItem> subItemsList;
    private SearchBar searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        Animation top_to_btm = AnimationUtils.loadAnimation(this, R.anim.top_to_btm);
        View view = findViewById(R.id.topCircle);
        view.setAnimation(top_to_btm);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String toolbarTitle = getIntent().getStringExtra("toolbarTitle");
        if (toolbarTitle != null) {
            getSupportActionBar().setTitle(toolbarTitle);
        }

        Intent intent = getIntent();
        subItemsList = intent.getParcelableArrayListExtra("subItemsList");

        searchBar = findViewById(R.id.searchBar);
        recyclerView = findViewById(R.id.viewAllRecyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        subItemAdapter = new ViewSubItemAdapter(this, subItemsList);
        recyclerView.setAdapter(subItemAdapter);

        searchBar.setOnClickListener(v -> startActivity(new Intent(this, SearchPdfActivity.class)));

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
