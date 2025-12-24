package com.sansan.vikashtutorial;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sansan.vikashtutorial.Home.HomePdfSubItem;
import com.sansan.vikashtutorial.Video.VideoItem;
import com.sansan.vikashtutorial.Video.VideoViewActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeVideoSubItemAdapter extends RecyclerView.Adapter<HomeVideoSubItemAdapter.SubItemViewHolder> {

    private Context context;
    private List<HomeVideoSubItem> subItemList;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    public HomeVideoSubItemAdapter(Context context, List<HomeVideoSubItem> subItemList) {
        this.subItemList = subItemList;
        this.context = context;
        this.firestore = firestore;
        this.currentUser = currentUser;
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public HomeVideoSubItemAdapter.SubItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_video_sub_item, parent, false);
        return new SubItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeVideoSubItemAdapter.SubItemViewHolder holder, int position) {

        HomeVideoSubItem subItem = subItemList.get(position);

        holder.textTitle.setText(subItem.getVideoTitle());
        holder.textDescription.setText(subItem.getVideoDescription());
        holder.textTimeline.setText(subItem.getVideoLength());

        Glide.with(context)
                .load(subItem.getVideoThumbnail())
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
                .into(holder.imgThumbnail);

        updateBookmarkIcon(holder.bookmarkButton, subItem.getVideoLink());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, VideoViewActivity.class);
            intent.putExtra("videoLink", subItem.getVideoLink());
            intent.putExtra("videoTitle", subItem.getVideoTitle());
            intent.putExtra("videoThumbnail", subItem.getVideoThumbnail());
            intent.putExtra("videoDesc", subItem.getVideoDescription());
            context.startActivity(intent);
        });

        holder.bookmarkButton.setOnClickListener(view -> handleBookmarkAction(holder.bookmarkButton, subItem));
    }

    @Override
    public int getItemCount() {
        return subItemList.size();
    }

    private void updateBookmarkIcon(ImageButton bookmarkButton, String videoLink) {
        if (currentUser == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        CollectionReference userBookmarkRef = firestore.collection("users")
                .document(userId).collection("VideoBookmarks");

        progressDialog.show();

        userBookmarkRef.whereEqualTo("bookedLink", videoLink).get()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null && !snapshot.isEmpty()) {
                            bookmarkButton.setImageResource(R.drawable.favorait);
                        } else {
                            bookmarkButton.setImageResource(R.drawable.unfavorait);
                        }
                    } else {
                        Toast.makeText(context, "Error checking bookmark: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleBookmarkAction(ImageButton bookmarkButton, HomeVideoSubItem subItem) {
        if (currentUser == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        CollectionReference userBookmarkRef = firestore.collection("users")
                .document(userId).collection("VideoBookmarks");

        progressDialog.show();

        userBookmarkRef.whereEqualTo("bookedLink", subItem.getVideoLink()).get()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null && !snapshot.isEmpty()) {
                            for (DocumentSnapshot document : snapshot.getDocuments()) {
                                document.getReference().delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(context, "Bookmark removed", Toast.LENGTH_SHORT).show();
                                            bookmarkButton.setImageResource(R.drawable.unfavorait);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(context, "Failed to remove bookmark: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            DocumentReference newBookmarkRef = userBookmarkRef.document();
                            Map<String, Object> bookmarkMap = new HashMap<>();
                            bookmarkMap.put("bookedThumbnail", subItem.getVideoThumbnail());
                            bookmarkMap.put("bookedTitle", subItem.getVideoTitle());
                            bookmarkMap.put("bookedLink", subItem.getVideoLink());

                            newBookmarkRef.set(bookmarkMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Bookmark added", Toast.LENGTH_SHORT).show();
                                        bookmarkButton.setImageResource(R.drawable.favorait);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Failed to add bookmark: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(context, "Error handling bookmark: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public class SubItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgThumbnail;
        private TextView textTitle, textDescription, textTimeline;
        private ImageButton bookmarkButton;

        public SubItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.thumbnailImg);
            textTitle = itemView.findViewById(R.id.titleVideo);
            textTitle.setSelected(true);
            textDescription = itemView.findViewById(R.id.descriptionVideo);
            textDescription.setSelected(true);
            textTimeline = itemView.findViewById(R.id.timeLineVideo);
            bookmarkButton = itemView.findViewById(R.id.videoBookmarkBtn);
        }
    }
}
