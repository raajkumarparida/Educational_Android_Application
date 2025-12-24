package com.sansan.vikashtutorial.Home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sansan.vikashtutorial.R;
import com.sansan.vikashtutorial.Activity.PdfViewAllActivity;

import java.util.ArrayList;
import java.util.List;

public class ItemHeaderAdapter extends RecyclerView.Adapter<ItemHeaderAdapter.ItemViewHolder> {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<ItemHeader> itemList;
    private Context context;

    public ItemHeaderAdapter(Context context, List<ItemHeader> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_home_item, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        ItemHeader item = itemList.get(i);
        itemViewHolder.tvItemTitle.setText(item.getItemTitle());

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                itemViewHolder.rvSubItem.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );

        itemViewHolder.tvItemViewAll.setOnClickListener(v -> {
            ItemHeader clickedItem = itemList.get(i);
            Intent intent = new Intent(itemViewHolder.itemView.getContext(), PdfViewAllActivity.class);
            intent.putParcelableArrayListExtra("subItemsList", new ArrayList<>(clickedItem.getSubItems()));
            intent.putExtra("toolbarTitle", clickedItem.getItemTitle());
            itemViewHolder.itemView.getContext().startActivity(intent);
        });


        layoutManager.setInitialPrefetchItemCount(item.getSubItems().size());

        HomePdfSubItemAdapter subItemAdapter = new HomePdfSubItemAdapter(context, item.getSubItems());

        itemViewHolder.rvSubItem.setLayoutManager(layoutManager);
        itemViewHolder.rvSubItem.setAdapter(subItemAdapter);
        itemViewHolder.rvSubItem.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemTitle, tvItemViewAll;
        private RecyclerView rvSubItem;

        ItemViewHolder(View itemView) {
            super(itemView);
            tvItemTitle = itemView.findViewById(R.id.itemTitle);
            tvItemTitle.setSelected(true);
            rvSubItem = itemView.findViewById(R.id.itemRecyclerview);
            tvItemViewAll = itemView.findViewById(R.id.viewAll);
        }
    }
}
