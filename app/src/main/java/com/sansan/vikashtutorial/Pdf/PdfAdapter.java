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
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sansan.vikashtutorial.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdfAdapter extends RecyclerView.Adapter<PdfAdapter.PdfViewHolder> {

    private Context context;
    private List<PdfItem> pdfItemList;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    public PdfAdapter(Context context, List<PdfItem> pdfItemList) {
        this.context = context;
        this.pdfItemList = pdfItemList;
        this.firestore = FirebaseFirestore.getInstance();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }
    @NonNull
    @Override
    public PdfAdapter.PdfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_note_pdf_item, parent, false);
        return new PdfViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfAdapter.PdfViewHolder holder, int position) {

        PdfItem pdfItem = pdfItemList.get(position);

        holder.subTitle.setText(pdfItem.getTopic());
        holder.subject.setText(pdfItem.getSubject());

        Glide.with(context)
                .load(pdfItem.getImg())
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
                .into(holder.subImg);

        updateBookmarkIcon(holder.bookmarkButton, pdfItem.getUrl());

        holder.itemView.setOnClickListener(view -> openNoteViewActivity(pdfItem.getUrl(), pdfItem.getTopic(), pdfItem.getImg(), pdfItem.getSubject()));

        holder.bookmarkButton.setOnClickListener(view -> handleBookmarkAction(holder.bookmarkButton, pdfItem));
    }

    @Override
    public int getItemCount() {
        return pdfItemList.size();
    }

    public void updateList(List<PdfItem> newList) {
        pdfItemList.clear();
        pdfItemList.addAll(newList);
        notifyDataSetChanged();
    }

    private void openNoteViewActivity(String pdfUrl, String pdfTopic, String pdfImg, String pdfSub) {
        Intent intent = new Intent(context, PdfViewActivity.class);
        intent.putExtra("pdfUrl", pdfUrl);
        intent.putExtra("pdfTopic", pdfTopic);
        intent.putExtra("pdfImg", pdfImg);
        intent.putExtra("pdfSub", pdfSub);
        context.startActivity(intent);
    }

    private void updateBookmarkIcon(ImageButton bookmarkButton, String url) {
        if (currentUser == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        CollectionReference userBookmarksRef = firestore.collection("users")
                .document(userId)
                .collection("NoteBookmarks");

        progressDialog.show();
        bookmarkButton.setVisibility(View.GONE);

        userBookmarksRef.whereEqualTo("bookedUrl", url)
                .get()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    bookmarkButton.setVisibility(View.VISIBLE);

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

    private void handleBookmarkAction(ImageButton bookmarkButton, PdfItem note) {
        if (currentUser == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        CollectionReference userBookmarksRef = firestore.collection("users")
                .document(userId)
                .collection("NoteBookmarks");

        bookmarkButton.setVisibility(View.GONE);
        progressDialog.show();

        userBookmarksRef.whereEqualTo("bookedUrl", note.getUrl())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null && !snapshot.isEmpty()) {
                            for (DocumentSnapshot document : snapshot.getDocuments()) {
                                userBookmarksRef.document(document.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(context, "Bookmark removed", Toast.LENGTH_SHORT).show();
                                            bookmarkButton.setImageResource(R.drawable.unfavorait);
                                            bookmarkButton.setVisibility(View.VISIBLE);
                                            progressDialog.dismiss();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(context, "Failed to remove bookmark: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            bookmarkButton.setVisibility(View.VISIBLE);
                                            progressDialog.dismiss();
                                        });
                            }
                        } else {
                            Map<String, Object> bookmarkMap = new HashMap<>();
                            bookmarkMap.put("bookedImg", note.getImg());
                            bookmarkMap.put("bookedTopic", note.getTopic());
                            bookmarkMap.put("bookedUrl", note.getUrl());

                            userBookmarksRef.add(bookmarkMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Bookmark added", Toast.LENGTH_SHORT).show();
                                        bookmarkButton.setImageResource(R.drawable.favorait);
                                        bookmarkButton.setVisibility(View.VISIBLE);
                                        progressDialog.dismiss();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Failed to add bookmark: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        bookmarkButton.setVisibility(View.VISIBLE);
                                        progressDialog.dismiss();
                                    });
                        }
                    } else {
                        Toast.makeText(context, "Error handling bookmark: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        bookmarkButton.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                });
    }

    public class PdfViewHolder extends RecyclerView.ViewHolder {

        TextView subTitle, subject;
        ImageView subImg;
        ImageButton bookmarkButton;
        public PdfViewHolder(@NonNull View itemView) {
            super(itemView);
            subTitle = itemView.findViewById(R.id.subTitle);
            subTitle.setSelected(true);
            subject = itemView.findViewById(R.id.subject);
            subject.setSelected(true);
            subImg = itemView.findViewById(R.id.subImg);
            bookmarkButton = itemView.findViewById(R.id.bookmarkNoteBtn);
        }
    }
}
