package com.sansan.vikashtutorial.Mock;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.search.SearchBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sansan.vikashtutorial.R;
import com.sansan.vikashtutorial.Search.SearchMockActivity;
import com.sansan.vikashtutorial.ViewAllMockActivity;

import java.util.ArrayList;
import java.util.List;

public class ChemistryMockFragment extends Fragment {

//    private ImageView noListMessage;
//    private RecyclerView recyclerView;
//    private MockAdapter adapter;
//    private List<MockItem> itemList;
//    private ProgressDialog progressDialog;
//    private SearchBar searchBar;
    private String userId;
    private ImageView noListMessageCHSE, noListMessageCBSE, noListMessageJEE, noListMessageNEET, noListMessageOUAT;
    private RecyclerView recyclerViewCHSE, recyclerViewCBSE, recyclerViewJEE, recyclerViewNEET, recyclerViewOUAT;
    private MockAdapter adapterCHSE, adapterCBSE, adapterJEE, adapterNEET, adapterOUAT;
    private List<MockItem> itemListCHSE, itemListCBSE, itemListJEE, itemListNEET, itemListOUAT;
    private ProgressDialog progressDialog;
    private RelativeLayout relativeLayout;
    private LinearLayout chseLinear, cbseLinear, jeeLinear, neetLinear, ouatLinear;
    private TextView chseHeader, cbseHeader, jeeHeader, neetHeader, ouatHeader;
    public ChemistryMockFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chemistry_mock, container, false);

//        searchBar = view.findViewById(R.id.searchBar);
//
//        noListMessage = view.findViewById(R.id.noListMessage);
//        recyclerView = view.findViewById(R.id.mockRecyclerview);
//
//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage("Loading...");
//        progressDialog.setCancelable(false);
//
//        searchBar.setOnClickListener(v -> startActivity(new Intent(getActivity(), SearchMockActivity.class)));
//
//        itemList = new ArrayList<>();
//
//        adapter = new MockAdapter(getContext(), itemList);
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
//        recyclerView.setAdapter(adapter);
//
//        fetchDataFromFireStore();
//
//        return view;
//    }
//
//
//    private void fetchDataFromFireStore() {
//        progressDialog.show();
//        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
//                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
//
//        if (userId == null) {
//            Toast.makeText(getContext(), "User not logged in.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        FirebaseFirestore.getInstance().collection("users").document(userId).get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        Boolean isClass11thSelected = documentSnapshot.getBoolean("class11th");
//                        Boolean isClass12thSelected = documentSnapshot.getBoolean("class12th");
//
//                        String categoryPath = null;
//
//                        if (Boolean.TRUE.equals(isClass11thSelected) && Boolean.FALSE.equals(isClass12thSelected)) {
//                            categoryPath = "11thChemistryMock";
//                        } else if (Boolean.TRUE.equals(isClass12thSelected) && Boolean.FALSE.equals(isClass11thSelected)) {
//                            categoryPath = "12thChemistryMock";
//                        }
//
//                        if (categoryPath != null) {
//                            fetchMockDataFromRealtimeDatabase(categoryPath);
//                        } else {
//                            Toast.makeText(getContext(), "Invalid class selection.", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(getContext(), "User data not found.", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//    }
//
//    private void fetchMockDataFromRealtimeDatabase(String categoryPath) {
//        DatabaseReference mockRef = FirebaseDatabase.getInstance().getReference(categoryPath);
//        progressDialog.show();
//
//        mockRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                progressDialog.dismiss();
//
//                itemList.clear();
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    MockItem mockItem = snapshot.getValue(MockItem.class);
//                    if (mockItem != null) {
//                        itemList.add(mockItem);
//                    }
//                }
//
//                adapter.notifyDataSetChanged();
//
//                if (itemList.isEmpty()) {
//                    noListMessage.setVisibility(View.VISIBLE);
//                    recyclerView.setVisibility(View.GONE);
//                } else {
//                    noListMessage.setVisibility(View.GONE);
//                    recyclerView.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                progressDialog.dismiss();
//                Toast.makeText(getContext(), "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

        userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (userId == null) {
            Toast.makeText(getContext(), "User not logged in.", Toast.LENGTH_SHORT).show();
            return view;
        }

        relativeLayout = view.findViewById(R.id.relativeLayout);

        chseHeader = view.findViewById(R.id.chseHeader);
        cbseHeader = view.findViewById(R.id.cbseHeader);
        jeeHeader = view.findViewById(R.id.jeeHeader);
        neetHeader = view.findViewById(R.id.neetHeader);
        ouatHeader = view.findViewById(R.id.ouatHeader);

        FirebaseFirestore.getInstance().collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean isClass11thSelected = documentSnapshot.getBoolean("class11th");
                        Boolean isClass12thSelected = documentSnapshot.getBoolean("class12th");

                        if (Boolean.TRUE.equals(isClass11thSelected)) {
                            // If class 11th is selected
                            setupHeaderClickListener(chseHeader, "11th", "CHSE");
                            setupHeaderClickListener(cbseHeader, "11th", "CBSE");
                            setupHeaderClickListener(jeeHeader, "11th", "JEE");
                            setupHeaderClickListener(neetHeader, "11th", "NEET");
                            setupHeaderClickListener(ouatHeader, "11th", "OUAT");
                        }

                        if (Boolean.TRUE.equals(isClass12thSelected)) {
                            // If class 12th is selected
                            setupHeaderClickListener(chseHeader, "12th", "CHSE");
                            setupHeaderClickListener(cbseHeader, "12th", "CBSE");
                            setupHeaderClickListener(jeeHeader, "12th", "JEE");
                            setupHeaderClickListener(neetHeader, "12th", "NEET");
                            setupHeaderClickListener(ouatHeader, "12th", "OUAT");
                        }
                    } else {
                        Toast.makeText(getContext(), "No data found for the user.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        chseLinear = view.findViewById(R.id.chseLinear);
        cbseLinear = view.findViewById(R.id.cbseLinear);
        jeeLinear = view.findViewById(R.id.jeeLinear);
        neetLinear = view.findViewById(R.id.neetLinear);
        ouatLinear = view.findViewById(R.id.ouatLinear);

        noListMessageCHSE = view.findViewById(R.id.noListMessage1);
        noListMessageCBSE = view.findViewById(R.id.noListMessage2);
        noListMessageJEE = view.findViewById(R.id.noListMessage3);
        noListMessageNEET = view.findViewById(R.id.noListMessage4);
        noListMessageOUAT = view.findViewById(R.id.noListMessage5);

        recyclerViewCHSE = view.findViewById(R.id.mockRecyclerviewCHSE);
        recyclerViewCBSE = view.findViewById(R.id.mockRecyclerviewCBSE);
        recyclerViewJEE = view.findViewById(R.id.mockRecyclerviewJEE);
        recyclerViewNEET = view.findViewById(R.id.mockRecyclerviewNEET);
        recyclerViewOUAT = view.findViewById(R.id.mockRecyclerviewOUAT);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        // Set search bar click listener

        // Initialize the lists for each category
        itemListCHSE = new ArrayList<>();
        itemListCBSE = new ArrayList<>();
        itemListJEE = new ArrayList<>();
        itemListNEET = new ArrayList<>();
        itemListOUAT = new ArrayList<>();

        // Initialize the adapters for each category
        adapterCHSE = new MockAdapter(getContext(), itemListCHSE);
        adapterCBSE = new MockAdapter(getContext(), itemListCBSE);
        adapterJEE = new MockAdapter(getContext(), itemListJEE);
        adapterNEET = new MockAdapter(getContext(), itemListNEET);
        adapterOUAT = new MockAdapter(getContext(), itemListOUAT);

        // Set the layout managers and adapters
        recyclerViewCHSE.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false));
        recyclerViewCBSE.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false));
        recyclerViewJEE.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false));
        recyclerViewNEET.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false));
        recyclerViewOUAT.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false));

        recyclerViewCHSE.setAdapter(adapterCHSE);
        recyclerViewCBSE.setAdapter(adapterCBSE);
        recyclerViewJEE.setAdapter(adapterJEE);
        recyclerViewNEET.setAdapter(adapterNEET);
        recyclerViewOUAT.setAdapter(adapterOUAT);

        fetchDataFromFireStore();

        return view;
    }

    private void setupHeaderClickListener(TextView headerView, String classLevel, String header) {
        headerView.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("MockPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selectedCategory", classLevel + "ChemistryMock");
            editor.putString("selectedHeader", header);
            editor.apply();

            Intent intent = new Intent(getContext(), ViewAllMockActivity.class);
            startActivity(intent);
        });
    }

    private void fetchDataFromFireStore() {
        progressDialog.show();
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(getContext(), "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance().collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean isClass11thSelected = documentSnapshot.getBoolean("class11th");
                        Boolean isClass12thSelected = documentSnapshot.getBoolean("class12th");

                        String categoryPath = null;

                        if (Boolean.TRUE.equals(isClass11thSelected) && Boolean.FALSE.equals(isClass12thSelected)) {
                            categoryPath = "11thChemistryMock";
                        } else if (Boolean.TRUE.equals(isClass12thSelected) && Boolean.FALSE.equals(isClass11thSelected)) {
                            categoryPath = "12thChemistryMock";
                        }

                        if (categoryPath != null) {
                            if (categoryPath.equals("11thChemistryMock")) {
                                fetchCHSEData();
                                fetchCBSEData();
                                fetchJEEData();
                                fetchNEETData();
                                fetchOUATData();
                            } else if (categoryPath.equals("12thChemistryMock")) {
                                fetch12thCHSEData();
                                fetch12thCBSEData();
                                fetch12thJEEData();
                                fetch12thNEETData();
                                fetch12thOUATData();
                            }
                        } else {
                            Toast.makeText(getContext(), "Invalid class selection.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void fetchCHSEData() {
        DatabaseReference mockRef = FirebaseDatabase.getInstance().getReference("11thChemistryMock/CHSE");
        fetchData(mockRef, itemListCHSE, adapterCHSE, recyclerViewCHSE, noListMessageCHSE, chseLinear);
    }

    private void fetchCBSEData() {
        DatabaseReference mockRef = FirebaseDatabase.getInstance().getReference("11thChemistryMock/CBSE");
        fetchData(mockRef, itemListCBSE, adapterCBSE, recyclerViewCBSE, noListMessageCBSE, cbseLinear);
    }

    private void fetchJEEData() {
        DatabaseReference mockRef = FirebaseDatabase.getInstance().getReference("11thChemistryMock/JEE");
        fetchData(mockRef, itemListJEE, adapterJEE, recyclerViewJEE, noListMessageJEE, jeeLinear);
    }

    private void fetchNEETData() {
        DatabaseReference mockRef = FirebaseDatabase.getInstance().getReference("11thChemistryMock/NEET");
        fetchData(mockRef, itemListNEET, adapterNEET, recyclerViewNEET, noListMessageNEET, neetLinear);
    }

    private void fetchOUATData() {
        DatabaseReference mockRef = FirebaseDatabase.getInstance().getReference("11thChemistryMock/OUAT");
        fetchData(mockRef, itemListOUAT, adapterOUAT, recyclerViewOUAT, noListMessageOUAT, ouatLinear);
    }

    private void fetch12thCHSEData() {
        DatabaseReference mockRef = FirebaseDatabase.getInstance().getReference("12thChemistryMock/CHSE");
        fetchData(mockRef, itemListCHSE, adapterCHSE, recyclerViewCHSE, noListMessageCHSE, chseLinear);
    }

    private void fetch12thCBSEData() {
        DatabaseReference mockRef = FirebaseDatabase.getInstance().getReference("12thChemistryMock/CBSE");
        fetchData(mockRef, itemListCBSE, adapterCBSE, recyclerViewCBSE, noListMessageCBSE, cbseLinear);
    }

    private void fetch12thJEEData() {
        DatabaseReference mockRef = FirebaseDatabase.getInstance().getReference("12thChemistryMock/JEE");
        fetchData(mockRef, itemListJEE, adapterJEE, recyclerViewJEE, noListMessageJEE, jeeLinear);
    }

    private void fetch12thNEETData() {
        DatabaseReference mockRef = FirebaseDatabase.getInstance().getReference("12thChemistryMock/NEET");
        fetchData(mockRef, itemListNEET, adapterNEET, recyclerViewNEET, noListMessageNEET, neetLinear);
    }

    private void fetch12thOUATData() {
        DatabaseReference mockRef = FirebaseDatabase.getInstance().getReference("12thChemistryMock/OUAT");
        fetchData(mockRef, itemListOUAT, adapterOUAT, recyclerViewOUAT, noListMessageOUAT, ouatLinear);
    }

    private void fetchData(DatabaseReference mockRef, List<MockItem> itemList, MockAdapter adapter, RecyclerView recyclerView, ImageView noListMessage, LinearLayout sectionLinearLayout) {
        progressDialog.show();
        relativeLayout.setVisibility(View.GONE);

        mockRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                relativeLayout.setVisibility(View.VISIBLE);

                itemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MockItem mockItem = snapshot.getValue(MockItem.class);
                    if (mockItem != null) {
                        itemList.add(mockItem);
                    }
                }

                adapter.notifyDataSetChanged();
                toggleVisibility(itemList, recyclerView, noListMessage, sectionLinearLayout);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleVisibility(List<MockItem> itemList, RecyclerView recyclerView, ImageView noListMessage, LinearLayout sectionLinearLayout) {
        if (itemList.isEmpty()) {
            noListMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            sectionLinearLayout.setVisibility(View.GONE);
        } else {
            noListMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            sectionLinearLayout.setVisibility(View.VISIBLE);
        }
    }
}