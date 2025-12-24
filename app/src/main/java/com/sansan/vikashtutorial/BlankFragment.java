package com.sansan.vikashtutorial;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sansan.vikashtutorial.Home.ItemHeader;
import com.sansan.vikashtutorial.Home.ItemHeaderAdapter;
import com.sansan.vikashtutorial.Mock.MockItem;

import java.util.ArrayList;
import java.util.List;

public class BlankFragment extends Fragment {

    private RecyclerView rcvTop;
    private ProgressDialog progressDialog;
    private MockHeaderAdapter itemAdapter;

    public BlankFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        rcvTop = view.findViewById(R.id.homeTopRecyclerview);
        rcvTop.setLayoutManager(new LinearLayoutManager(getActivity()));

        fetchMockItems();
        return view;
    }

    private void fetchMockItems() {
        progressDialog.show();

        DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference("Demo");
        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (dataSnapshot.exists()) {
                    List<MockHeader> itemList = new ArrayList<>();
                    for (DataSnapshot document : dataSnapshot.getChildren()) {
                        String title = document.child("title").getValue(String.class);
                        if (title != null) {
                            fetchSubMockItems(document.child("subItemList"), title, itemList);
                        }
                    }
                } else {
                    // Handle empty data case
                    Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                // Handle error
                Toast.makeText(getContext(), "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchSubMockItems(DataSnapshot subItemsSnapshot, String title, List<MockHeader> itemList) {
        if (subItemsSnapshot.exists()) {
            List<MockItem> subItemList = new ArrayList<>();

            for (DataSnapshot document : subItemsSnapshot.getChildren()) {
                String id = document.child("id").getValue(String.class);
                String subItemTitle = document.child("title").getValue(String.class);
                String subtitle = document.child("subtitle").getValue(String.class);
                String time = document.child("time").getValue(String.class);
                String img = document.child("img").getValue(String.class);

                if (subItemTitle != null) {
                    subItemList.add(new MockItem(id, subItemTitle, subtitle, time, img, new ArrayList<>()));
                }
            }

            itemList.add(new MockHeader(title, subItemList));

            if (itemAdapter == null) {
                itemAdapter = new MockHeaderAdapter(getContext(), itemList);
                rcvTop.setAdapter(itemAdapter);
            } else {
                itemAdapter.notifyDataSetChanged();
            }
        } else {
            Toast.makeText(getContext(), "No sub-items found for: " + title, Toast.LENGTH_SHORT).show();
        }

    }

}