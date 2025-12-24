package com.sansan.vikashtutorial.Home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.sansan.vikashtutorial.Activity.PYQActivity;
import com.sansan.vikashtutorial.Activity.PracticeSectionActivity;
import com.sansan.vikashtutorial.R;

import java.util.List;

public class HomeContentAdapter extends RecyclerView.Adapter<HomeContentAdapter.ContentViewHolder> {

    private Context context;
    private List<HomeContentItem> contentItemList;
    private ProgressDialog progressDialog;

    public HomeContentAdapter(Context context, List<HomeContentItem> contentItemList) {
        this.contentItemList = contentItemList;
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_content_item, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeContentAdapter.ContentViewHolder holder, int position) {

        HomeContentItem contentItem = contentItemList.get(position);

        holder.tvContentItemTitle.setText(contentItem.getSubItemTitle());
        holder.tvContentItemSub.setText(contentItem.getSubItemDesc());

        Glide.with(context)
                .load(contentItem.getSubItemImage())
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher))
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
                .into(holder.tvContentItemImg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String activityCode = contentItem.getActivityCode();

                Intent intent = null;

                if (activityCode.equals("1")) {
                    intent = new Intent(context, PYQActivity.class);
                } else if (activityCode.equals("2")) {
                    intent = new Intent(context, PracticeSectionActivity.class);
                } else {
                    Toast.makeText(context, "Unknown activity!", Toast.LENGTH_SHORT).show();
                }

                if (intent != null) {
                    context.startActivity(intent);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return contentItemList.size();
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView tvContentItemTitle;
        TextView tvContentItemSub;
        ImageView tvContentItemImg;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContentItemTitle = itemView.findViewById(R.id.contentItemTitle);
            tvContentItemSub = itemView.findViewById(R.id.contentItemSubject);
            tvContentItemImg = itemView.findViewById(R.id.contentItemImg);
        }
    }
}
