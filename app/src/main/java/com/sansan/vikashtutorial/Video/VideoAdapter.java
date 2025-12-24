package com.sansan.vikashtutorial.Video;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sansan.vikashtutorial.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private Context context;
    private List<VideoItem> videoItemList;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    public VideoAdapter(Context context, List<VideoItem> videoItemList) {
        this.context = context;
        this.videoItemList = videoItemList;
        this.firestore = FirebaseFirestore.getInstance();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_video_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoItem videoItem = videoItemList.get(position);

        holder.textTitle.setText(videoItem.getTitle());
        holder.textDescription.setText(videoItem.getDescription());
        holder.textTimeline.setText(videoItem.getFormattedLength());

        Glide.with(context)
                .load(videoItem.getThumbnail())
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

        updateBookmarkIcon(holder.bookmarkButton, videoItem.getLink());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, VideoViewActivity.class);
            intent.putExtra("videoLink", videoItem.getLink());
            intent.putExtra("videoTitle", videoItem.getTitle());
            intent.putExtra("videoThumbnail", videoItem.getThumbnail());
            intent.putExtra("videoDesc", videoItem.getDescription());
            intent.putExtra("videoLength", videoItem.getLength());
            context.startActivity(intent);
        });

        holder.bookmarkButton.setOnClickListener(view -> handleBookmarkAction(holder.bookmarkButton, videoItem));

    }

    @Override
    public int getItemCount() {
        return videoItemList.size();
    }

    public void updateList(List<VideoItem> newList) {
        videoItemList.clear();
        videoItemList.addAll(newList);
        notifyDataSetChanged();
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

    private void handleBookmarkAction(ImageButton bookmarkButton, VideoItem videoItem) {
        if (currentUser == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        CollectionReference userBookmarkRef = firestore.collection("users")
                .document(userId).collection("VideoBookmarks");

        progressDialog.show();

        userBookmarkRef.whereEqualTo("bookedLink", videoItem.getLink()).get()
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
                            bookmarkMap.put("bookedThumbnail", videoItem.getThumbnail());
                            bookmarkMap.put("bookedTitle", videoItem.getTitle());
                            bookmarkMap.put("bookedLink", videoItem.getLink());

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

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgThumbnail;
        private TextView textTitle, textDescription, textTimeline;
        private ImageButton bookmarkButton;

        public VideoViewHolder(@NonNull View itemView) {
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
