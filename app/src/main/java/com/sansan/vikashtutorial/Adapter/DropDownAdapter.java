package com.sansan.vikashtutorial.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sansan.vikashtutorial.Item.DropDownItem;
import com.sansan.vikashtutorial.R;

import java.util.List;

public class DropDownAdapter extends RecyclerView.Adapter<DropDownAdapter.YourExamViewHolder> {

    private List<DropDownItem> mList;
    private Context context;

    public DropDownAdapter(Context context, List<DropDownItem> mList) {
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public DropDownAdapter.YourExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_your_exam, parent, false);
        return new YourExamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DropDownAdapter.YourExamViewHolder holder, int position) {
        DropDownItem model = mList.get(position);
        holder.examTitle.setText(model.getExamTitle());

        Glide.with(context)
                .load(model.getExamImg())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.backImg);

        boolean isExpandable = model.isExpandable();
        holder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

        DropDownListAdapter adapter = new DropDownListAdapter(context, model.getExamTitle(), model.getSubjectNames(), model.getSubjectUrls());
        holder.nestedRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.nestedRecyclerView.setHasFixedSize(true);
        holder.nestedRecyclerView.setAdapter(adapter);

        if (isExpandable) {
            holder.mArrowImage.setImageResource(R.drawable.baseline_arrow_drop_up_24);
        } else {
            holder.mArrowImage.setImageResource(R.drawable.baseline_arrow_drop_down_24);
        }

        holder.linearLayout.setOnClickListener(v -> {
            model.setExpandable(!model.isExpandable());
            notifyItemChanged(holder.getAdapterPosition());
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class YourExamViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout linearLayout;
        private RelativeLayout expandableLayout;
        private TextView examTitle;
        private ImageView mArrowImage, backImg;
        private RecyclerView nestedRecyclerView;

        public YourExamViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linear_layout);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);
            examTitle = itemView.findViewById(R.id.examTitle);
            examTitle.setSelected(true);
            mArrowImage = itemView.findViewById(R.id.arro_imageview);
            backImg = itemView.findViewById(R.id.background_image);
            nestedRecyclerView = itemView.findViewById(R.id.child_rv);
        }
    }
}
