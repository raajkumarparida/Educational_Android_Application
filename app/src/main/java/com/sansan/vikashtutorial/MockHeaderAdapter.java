package com.sansan.vikashtutorial;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sansan.vikashtutorial.Mock.MockAdapter;
import com.sansan.vikashtutorial.R;
import com.sansan.vikashtutorial.Activity.PdfViewAllActivity;

import java.util.ArrayList;
import java.util.List;

public class MockHeaderAdapter extends RecyclerView.Adapter<MockHeaderAdapter.MockViewHolder> {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<MockHeader> itemList;
    private Context context;

    public MockHeaderAdapter(Context context, List<MockHeader> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MockViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_home_item, viewGroup, false);
        return new MockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MockViewHolder mockViewHolder, int i) {
        MockHeader item = itemList.get(i);
        mockViewHolder.tvItemTitle.setText(item.getItemTitle());

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                mockViewHolder.rvSubItem.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );

        mockViewHolder.tvItemViewAll.setOnClickListener(v -> {
            MockHeader clickedItem = itemList.get(i);
            Intent intent = new Intent(mockViewHolder.itemView.getContext(), PdfViewAllActivity.class);
            intent.putParcelableArrayListExtra("subItemsList", new ArrayList<>(clickedItem.getSubItems()));
            intent.putExtra("toolbarTitle", clickedItem.getItemTitle());
            mockViewHolder.itemView.getContext().startActivity(intent);
        });

        layoutManager.setInitialPrefetchItemCount(item.getSubItems().size());

        MockAdapter subItemAdapter = new MockAdapter(context, item.getSubItems());

        mockViewHolder.rvSubItem.setLayoutManager(layoutManager);
        mockViewHolder.rvSubItem.setAdapter(subItemAdapter);
        mockViewHolder.rvSubItem.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class MockViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemTitle, tvItemViewAll;
        private RecyclerView rvSubItem;

        MockViewHolder(View itemView) {
            super(itemView);
            tvItemTitle = itemView.findViewById(R.id.itemTitle);
            tvItemTitle.setSelected(true);
            rvSubItem = itemView.findViewById(R.id.itemRecyclerview);
            tvItemViewAll = itemView.findViewById(R.id.viewAll);
        }
    }
}
