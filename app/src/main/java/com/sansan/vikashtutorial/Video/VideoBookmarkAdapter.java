package com.sansan.vikashtutorial.Video;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sansan.vikashtutorial.R;

import java.util.List;

public class VideoBookmarkAdapter extends RecyclerView.Adapter<VideoBookmarkAdapter.VideoViewHolder> {

    private Context context;
    private List<VideoBookmarkItem> bookmarkList;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;


    public VideoBookmarkAdapter(Context context, List<VideoBookmarkItem> bookmarkList) {
        this.context = context;
        this.bookmarkList = bookmarkList;
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        this.firestore = FirebaseFirestore.getInstance();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_video_bookmark, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoBookmarkItem bookmark = bookmarkList.get(position);

        holder.bookmarkTitle.setText(bookmark.getBookedTitle());

        Glide.with(context)
                .load(bookmark.getBookedThumbnail())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        if (e != null) {
                            for (Throwable t : e.getRootCauses()) {
                                Log.e("GlideError", "Root cause: ", t);
                            }
                            e.logRootCauses("GlideError");
                        }

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        return false;
                    }
                })
                .into(holder.bookmarkThumbnail);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, VideoViewActivity.class);
            intent.putExtra("videoTitle", bookmark.getBookedTitle());
            intent.putExtra("videoLink", bookmark.getBookedLink());
            context.startActivity(intent);
        });

        holder.bookmarkDeleteButton.setOnClickListener(view -> deleteBookmark(holder, bookmark));


        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }


    private void deleteBookmark(VideoViewHolder holder, VideoBookmarkItem bookmark) {
        if (currentUser == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        CollectionReference userBookmarkRef = firestore.collection("users")
                .document(userId)
                .collection("VideoBookmarks");

        showGlobalProgressBar();

        userBookmarkRef.whereEqualTo("bookedLink", bookmark.getBookedLink())
                .get()
                .addOnCompleteListener(task -> {
                    hideGlobalProgressBar();
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null && !snapshot.isEmpty()) {
                            for (DocumentSnapshot document : snapshot.getDocuments()) {
                                document.getReference().delete().addOnCompleteListener(deleteTask -> {
                                    if (deleteTask.isSuccessful()) {
                                        bookmarkList.remove(bookmark);
                                        notifyDataSetChanged();
                                        Toast.makeText(context, "Bookmark removed", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Failed to remove bookmark", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(context, "Bookmark not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showGlobalProgressBar() {
        if (progressDialog != null) {
            progressDialog.show();
        }
    }

    private void hideGlobalProgressBar() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView bookmarkThumbnail;
        TextView bookmarkTitle;
        ImageButton bookmarkDeleteButton;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            bookmarkThumbnail = itemView.findViewById(R.id.bookmarkThumbnail);
            bookmarkTitle = itemView.findViewById(R.id.bookmarkTitle);
            bookmarkTitle.setSelected(true);
            bookmarkDeleteButton = itemView.findViewById(R.id.bookmarkDeleteBtn);
        }
    }
}
