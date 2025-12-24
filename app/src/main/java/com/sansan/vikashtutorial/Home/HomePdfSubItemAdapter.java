package com.sansan.vikashtutorial.Home;

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
import com.sansan.vikashtutorial.Pdf.PdfViewActivity;
import com.sansan.vikashtutorial.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePdfSubItemAdapter extends RecyclerView.Adapter<HomePdfSubItemAdapter.SubItemViewHolder> {

    private List<HomePdfSubItem> subItemList;
    private Context context;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    public HomePdfSubItemAdapter(Context context, List<HomePdfSubItem> subItemList) {
        this.context = context;
        this.subItemList = subItemList;
        this.firestore = FirebaseFirestore.getInstance();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public SubItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_home_pdf_sub_item, viewGroup, false);
        return new SubItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubItemViewHolder subItemViewHolder, int i) {
        HomePdfSubItem subItem = subItemList.get(i);

        subItemViewHolder.subItemTitle.setText(subItem.getSubItemTitle());
        subItemViewHolder.subItemSub.setText(subItem.getSubItemDesc());

        Glide.with(context)
                .load(subItem.getSubItemImage())
                .apply(new RequestOptions()
                        .placeholder(R.color.deep_moss_green)
                        .error(R.color.deep_moss_green))
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
                .into(subItemViewHolder.subItemImg);

        updateBookmarkIcon(subItemViewHolder.subBookmarkBtn, subItem.getSubItemUrl());

        subItemViewHolder.itemView.setOnClickListener(view -> openNoteViewActivity(subItem.getSubItemUrl(), subItem.getSubItemTitle(),subItem.getSubItemImage(), subItem.getSubItemDesc()));

        subItemViewHolder.subBookmarkBtn.setOnClickListener(view -> handleBookmarkAction(subItemViewHolder.subBookmarkBtn, subItem));

    }


    @Override
    public int getItemCount() {
        return subItemList.size();
    }

    public void updateList(List<HomePdfSubItem> newList) {
        subItemList.clear();
        subItemList.addAll(newList);
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

    private void handleBookmarkAction(ImageButton bookmarkButton, HomePdfSubItem subItem) {
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

        userBookmarksRef.whereEqualTo("bookedUrl", subItem.getSubItemUrl())
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
                            bookmarkMap.put("bookedImg", subItem.getSubItemImage());
                            bookmarkMap.put("bookedTopic", subItem.getSubItemTitle());
                            bookmarkMap.put("bookedUrl", subItem.getSubItemUrl());

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

    class SubItemViewHolder extends RecyclerView.ViewHolder {
       private TextView subItemTitle;
       private TextView subItemSub;
       private ImageView subItemImg;
       private ImageButton subBookmarkBtn;

        SubItemViewHolder(View itemView) {
            super(itemView);
            subItemTitle = itemView.findViewById(R.id.subItemTitle);
            subItemTitle.setSelected(true);
            subItemSub = itemView.findViewById(R.id.subItemSubject);
            subItemSub.setSelected(true);
            subItemImg = itemView.findViewById(R.id.subItemImg);
            subBookmarkBtn = itemView.findViewById(R.id.bookmarkBtn);
        }
    }
}
