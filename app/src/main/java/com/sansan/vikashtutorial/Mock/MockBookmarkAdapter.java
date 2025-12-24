package com.sansan.vikashtutorial.Mock;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.sansan.vikashtutorial.R;

import java.util.List;

public class MockBookmarkAdapter extends RecyclerView.Adapter<MockBookmarkAdapter.MockBookmarkViewHolder> {

    private Context context;
    private List<MockBookmarkItem> mockBookmarkItemList;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    public MockBookmarkAdapter( Context context, List<MockBookmarkItem> mockBookmarkItemList) {
        this.context = context;
        this.mockBookmarkItemList = mockBookmarkItemList;
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        this.firestore = FirebaseFirestore.getInstance();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @NonNull
    @Override
    public MockBookmarkAdapter.MockBookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_mock_bookmark, parent, false);
        return new MockBookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MockBookmarkAdapter.MockBookmarkViewHolder holder, int position) {

        MockBookmarkItem quiz = mockBookmarkItemList.get(position);
        holder.quizQuestion.setText(quiz.getQuestion());
        holder.quizAnswer.setText(quiz.getCorrectAnswer());

        holder.quizDeleteBtn.setOnClickListener(v -> deleteBookmark(quiz, position));

        if (progressDialog != null) {
            progressDialog.dismiss();
        }

    }

    @Override
    public int getItemCount() {
        return mockBookmarkItemList.size();
    }

    private void deleteBookmark(MockBookmarkItem quiz, int position) {
        String documentId = quiz.getDocumentId();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (uid == null || documentId == null) {
            Toast.makeText(context, "Error: User or quiz data is invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        MockBookmarkItem quizToRemove = mockBookmarkItemList.get(position);

        showGlobalProgressBar();

        mockBookmarkItemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mockBookmarkItemList.size());

        FirebaseFirestore.getInstance().collection("users").document(uid)
                .collection("QuizBookmarked").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    hideGlobalProgressBar();
                    Toast.makeText(context, "Quiz deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    hideGlobalProgressBar();
                    mockBookmarkItemList.add(position, quizToRemove);
                    notifyItemInserted(position);
                    notifyItemRangeChanged(position, mockBookmarkItemList.size());
                    Toast.makeText(context, "Error deleting quiz: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public class MockBookmarkViewHolder extends RecyclerView.ViewHolder {

        TextView quizQuestion, quizAnswer;
        ImageButton quizDeleteBtn;

        public MockBookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            quizQuestion = itemView.findViewById(R.id.quizQuestion);
            quizAnswer = itemView.findViewById(R.id.quizAnswer);
            quizDeleteBtn = itemView.findViewById(R.id.quizDeleteBtn);
        }
    }
}
