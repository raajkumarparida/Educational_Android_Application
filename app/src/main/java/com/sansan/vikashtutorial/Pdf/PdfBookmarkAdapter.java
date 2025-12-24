package com.sansan.vikashtutorial.Pdf;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sansan.vikashtutorial.R;

import java.util.List;

public class PdfBookmarkAdapter extends RecyclerView.Adapter<PdfBookmarkAdapter.PdfViewHolder> {

    private Context context;
    private List<PdfBookmarkItem> bookmarkList;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    public PdfBookmarkAdapter(Context context, List<PdfBookmarkItem> bookmarkList) {
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
    public PdfBookmarkAdapter.PdfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_pdf_bookmark, parent, false);
        return new PdfViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfBookmarkAdapter.PdfViewHolder holder, int position) {

        PdfBookmarkItem bookmarkItem = bookmarkList.get(position);
        holder.subTitle.setText(bookmarkItem.getBookedTopic());

        Glide.with(context)
                .load(bookmarkItem.getBookedImg())
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
                .into(holder.subImg);


        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, PdfViewActivity.class);
            intent.putExtra("pdfUrl", bookmarkItem.getBookedUrl());
            intent.putExtra("pdfTopic", bookmarkItem.getBookedTopic());
            context.startActivity(intent);
        });

        holder.deleteBookmarkButton.setOnClickListener(view -> {
            deleteBookmark(holder, bookmarkItem);
        });
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    private void deleteBookmark(PdfViewHolder holder, PdfBookmarkItem bookmarkItem) {
        if (currentUser == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference userBookmarkRef = firestore.collection("users").document(userId).collection("NoteBookmarks");

        showProgressBar();
        userBookmarkRef.whereEqualTo("bookedUrl", bookmarkItem.getBookedUrl()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DocumentReference docRef = document.getReference();
                    docRef.delete().addOnCompleteListener(deleteTask -> {
                        hideProgressBar(); // Hide the progress bar
                        if (deleteTask.isSuccessful()) {
                            bookmarkList.remove(bookmarkItem);
                            notifyDataSetChanged(); // Refresh adapter
                            Toast.makeText(context, "Bookmark removed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to remove bookmark", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                hideProgressBar();
                Toast.makeText(context, "Bookmark not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            hideProgressBar();
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void showProgressBar() {
        if (progressDialog != null) {
            progressDialog.show();
        }
    }

    private void hideProgressBar() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    public class PdfViewHolder extends RecyclerView.ViewHolder {

        private TextView subTitle;
        private ImageView subImg;
        private ImageButton deleteBookmarkButton;
        public PdfViewHolder(@NonNull View itemView) {
            super(itemView);

            subTitle = itemView.findViewById(R.id.bookedSubTitle);
            subTitle.setSelected(true);
            subImg = itemView.findViewById(R.id.bookedSubImg);
            deleteBookmarkButton = itemView.findViewById(R.id.deleteBookmarkBtn);
        }
    }
}
