package com.sansan.vikashtutorial.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sansan.vikashtutorial.Pdf.PdfItem;
import com.sansan.vikashtutorial.Pdf.PdfViewActivity;
import com.sansan.vikashtutorial.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DropDownListAdapter extends RecyclerView.Adapter<DropDownListAdapter.YourExamListViewHolder> {

    private List<String> subjectNames;
    private List<String> pdfUrls;
    private String examTitle;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private Context context;

    public DropDownListAdapter(Context context, String examTitle, List<String> subjectNames, List<String> pdfUrls) {
        this.examTitle = examTitle;
        this.subjectNames = subjectNames;
        this.pdfUrls = pdfUrls;
        this.context = context;
        this.firestore = FirebaseFirestore.getInstance();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public DropDownListAdapter.YourExamListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_your_exam_list, parent, false);
        return new YourExamListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DropDownListAdapter.YourExamListViewHolder holder, int position) {
        String subjectName = subjectNames.get(position);
        String pdfUrl = pdfUrls.get(position);
        String title = subjectName + " - " + examTitle;

        holder.mTv.setText(subjectName);

        // Create PdfItem instance
        PdfItem pdfItem = new PdfItem();
        pdfItem.setImg(null); // Set appropriate image resource or leave as null
        pdfItem.setTopic(title);
        pdfItem.setUrl(pdfUrl);

        holder.itemView.setOnClickListener(v -> {
            if (pdfUrl != null && !pdfUrl.isEmpty()) {
                Intent intent = new Intent(holder.itemView.getContext(), PdfViewActivity.class);
                intent.putExtra("pdfUrl", pdfUrl);
                intent.putExtra("pdfTopic", title);
                holder.itemView.getContext().startActivity(intent);
            } else {
                Toast.makeText(holder.itemView.getContext(), "No PDF available for this subject", Toast.LENGTH_SHORT).show();
            }
        });

        updateBookmarkIcon(holder.bookmarkBtn, pdfUrl);
        holder.bookmarkBtn.setOnClickListener(view -> handleBookmarkAction(holder.bookmarkBtn, pdfItem));
    }

    @Override
    public int getItemCount() {
        return subjectNames.size();
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

    public class YourExamListViewHolder extends RecyclerView.ViewHolder {
        private TextView mTv;
        private ImageButton bookmarkBtn;

        public YourExamListViewHolder(@NonNull View itemView) {
            super(itemView);
            mTv = itemView.findViewById(R.id.nestedItemTv);
            mTv.setSelected(true);
            bookmarkBtn = itemView.findViewById(R.id.bookmarkNoteBtn);
        }
    }
}
