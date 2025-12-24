package com.sansan.vikashtutorial.Search;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sansan.vikashtutorial.R;
import com.sansan.vikashtutorial.Video.VideoAdapter;
import com.sansan.vikashtutorial.Video.VideoItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SearchVideoActivity extends AppCompatActivity {

    private ImageView noListMessage;
    private RecyclerView recyclerView;
    private ListView suggestionsListView;
    private VideoAdapter videoAdapter;
    private List<VideoItem> videoList; // Combined list of videos
    private List<VideoItem> backupVideoList; // Backup of the original video list
    private List<String> suggestionList; // Titles for suggestions
    private ArrayAdapter<String> suggestionAdapter;
    private FirebaseFirestore firestore;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_video);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        noListMessage = findViewById(R.id.noListMessage);
        recyclerView = findViewById(R.id.searchRecyclerview);
        suggestionsListView = findViewById(R.id.suggestionsListView);
        searchView = findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        videoList = new ArrayList<>();
        backupVideoList = new ArrayList<>();
        suggestionList = new ArrayList<>();
        videoAdapter = new VideoAdapter(this, videoList);
        recyclerView.setAdapter(videoAdapter);

        firestore = FirebaseFirestore.getInstance();

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

        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch user data to determine the selected class
        firestore.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean isClass11thSelected = documentSnapshot.getBoolean("class11th");
                        Boolean isClass12thSelected = documentSnapshot.getBoolean("class12th");

                        if (Boolean.TRUE.equals(isClass11thSelected) && Boolean.FALSE.equals(isClass12thSelected)) {
                            fetchDataFromFirestore(Arrays.asList("11thOriyaLecture", "11thEnglishLecture", "11thPhysicsLecture", "11thChemistryLecture", "11thMathLecture", "11thBotanyLecture", "11thZoologyLecture"));
                        } else if (Boolean.TRUE.equals(isClass12thSelected) && Boolean.FALSE.equals(isClass11thSelected)) {
                            fetchDataFromFirestore(Arrays.asList("12thOriyaLecture", "12thEnglishLecture", "12thPhysicsLecture", "12thChemistryLecture", "12thMathLecture", "12thBotanyLecture", "12thZoologyLecture"));
                        } else {
                            Toast.makeText(this, "Invalid class selection.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());


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


        suggestionsListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTitle = suggestionAdapter.getItem(position);
            searchView.setQuery("", false);
            filterList(selectedTitle);
            suggestionsListView.setVisibility(View.GONE);
        });

    }

    private void fetchDataFromFirestore(List<String> collectionNames) {
        for (String collectionName : collectionNames) {
            CollectionReference videoCollectionRef = firestore.collection(collectionName);

            videoCollectionRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        VideoItem videoItem = document.toObject(VideoItem.class);
                        videoList.add(videoItem);
                        suggestionList.add(videoItem.getTitle()); // Add title to suggestions
                    }

                    // Shuffle the list to randomize the order
                    shuffleVideoList();

                    // Save a backup copy of the shuffled list
                    backupVideoList.clear();
                    backupVideoList.addAll(videoList);

                    // Update suggestions adapter
                    suggestionAdapter.notifyDataSetChanged();
                    videoAdapter.notifyDataSetChanged();

                    // Show no list message if the list is empty
                    if (videoList.isEmpty()) {
                        noListMessage.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        noListMessage.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }


    private void shuffleVideoList() {
        Collections.shuffle(videoList);
    }

    private void filterList(String query) {
        List<VideoItem> filteredList = new ArrayList<>();

        for (VideoItem item : backupVideoList) {
            if (item.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()) {
            noListMessage.setVisibility(View.VISIBLE);  // Show no list message
            recyclerView.setVisibility(View.GONE);
        } else {
            noListMessage.setVisibility(View.GONE);  // Hide no list message
            recyclerView.setVisibility(View.VISIBLE);
        }

        videoAdapter.updateList(filteredList);
        suggestionsListView.setVisibility(View.GONE); // Hide suggestions after filtering
    }


    private void restoreOriginalList() {
        videoList.clear();
        videoList.addAll(backupVideoList);
        videoAdapter.notifyDataSetChanged();
        noListMessage.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showSuggestions(String query) {
        // Clear previous suggestions
        suggestionList.clear();

        for (VideoItem item : backupVideoList) {
            if (item.getTitle().toLowerCase().contains(query.toLowerCase())) {
                suggestionList.add(item.getTitle());
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
