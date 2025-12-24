package com.sansan.vikashtutorial.Search;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sansan.vikashtutorial.Mock.MockAdapter;
import com.sansan.vikashtutorial.Mock.MockItem;
import com.sansan.vikashtutorial.R;
import com.sansan.vikashtutorial.ViewAllMockAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchMockActivity extends AppCompatActivity {

    private ImageView noListMessage;
    private RecyclerView recyclerView;
    private ListView suggestionsListView;
    private ViewAllMockAdapter mockAdapter;
    private List<MockItem> mockList;
    private List<MockItem> backupMockList;
    private List<String> suggestionList;
    private ArrayAdapter<String> suggestionAdapter;
    private DatabaseReference databaseReference;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_mock);

        Animation top_to_btm = AnimationUtils.loadAnimation(this, R.anim.top_to_btm);
        View topView = findViewById(R.id.topCircle);
        topView.setAnimation(top_to_btm);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        noListMessage = findViewById(R.id.noListMessage);
        recyclerView = findViewById(R.id.searchRecyclerview);
        suggestionsListView = findViewById(R.id.suggestionsListView);
        searchView = findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        mockList = new ArrayList<>();
        backupMockList = new ArrayList<>();
        suggestionList = new ArrayList<>();
        mockAdapter = new ViewAllMockAdapter(this, mockList);
        recyclerView.setAdapter(mockAdapter);


        SharedPreferences preferences = getSharedPreferences("MockSearchPrefs", Context.MODE_PRIVATE);
        String selectedCategory = preferences.getString("selectedCategory", null);
        String selectedHeader = preferences.getString("selectedHeader", null);

        // Initialize database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Fetch data based on the selected category and header
        if (selectedCategory != null && selectedHeader != null) {
            fetchFilteredData(selectedCategory, selectedHeader);
        } else {
            Toast.makeText(this, "No category or header selected.", Toast.LENGTH_SHORT).show();
        }

        // Set up the suggestion list adapter
        suggestionAdapter = new ArrayAdapter<String>(this, R.layout.layout_suggestion_item, R.id.suggestion_text, suggestionList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView suggestionText = view.findViewById(R.id.suggestion_text);
                ImageView suggestionIcon = view.findViewById(R.id.suggestion_icon);

                String suggestion = getItem(position);
                suggestionText.setText(suggestion);
                suggestionIcon.setImageResource(R.drawable.searchiconsvg);

                return view;
            }
        };
        suggestionsListView.setAdapter(suggestionAdapter);

        // Handle search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterList(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    restoreOriginalList();
                    suggestionsListView.setVisibility(View.GONE);
                } else {
                    showSuggestions(newText);
                }
                return false;
            }
        });

        // Handle suggestion list click events
        suggestionsListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTitle = suggestionAdapter.getItem(position);
            searchView.setQuery("", false);
            filterList(selectedTitle);
            suggestionsListView.setVisibility(View.GONE);
        });
    }

    private void fetchFilteredData(String category, String header) {
        databaseReference.child(category).child(header).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mockList.clear();
                suggestionList.clear();

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    MockItem mockItem = itemSnapshot.getValue(MockItem.class);
                    if (mockItem != null) {
                        mockList.add(mockItem);
                        suggestionList.add(mockItem.getTitle());
                    }
                }

                backupMockList.clear();
                backupMockList.addAll(mockList);
                suggestionAdapter.notifyDataSetChanged();
                mockAdapter.notifyDataSetChanged();

                if (mockList.isEmpty()) {
                    noListMessage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    noListMessage.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchMockActivity.this, "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterList(String query) {
        List<MockItem> filteredList = new ArrayList<>();

        for (MockItem item : backupMockList) {
            if (item.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    item.getSubtitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()) {
            noListMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noListMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        mockAdapter.updateList(filteredList);
        suggestionsListView.setVisibility(View.GONE);
    }

    private void restoreOriginalList() {
        mockList.clear();
        mockList.addAll(backupMockList);
        mockAdapter.notifyDataSetChanged();
        noListMessage.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showSuggestions(String query) {
        suggestionList.clear();

        for (MockItem item : backupMockList) {
            if (item.getTitle().toLowerCase().contains(query.toLowerCase())) {
                suggestionList.add(item.getTitle());
                suggestionList.add(item.getSubtitle());
            }
        }

        if (suggestionList.isEmpty()) {
            suggestionsListView.setVisibility(View.GONE);
        } else {
            suggestionsListView.setVisibility(View.VISIBLE);
            suggestionAdapter.notifyDataSetChanged();
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
