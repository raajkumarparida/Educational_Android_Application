package com.sansan.vikashtutorial.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sansan.vikashtutorial.R;

import java.util.List;

public class QuestionSummaryAdapter extends RecyclerView.Adapter<QuestionSummaryAdapter.ViewHolder> {

    private Context context;
    private List<String> userAnswers;
    private int questionCount;

    public QuestionSummaryAdapter(Context context, List<String> userAnswers, int questionCount) {
        this.context = context;
        this.userAnswers = userAnswers;
        this.questionCount = questionCount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sunnery_question_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String userAnswer = userAnswers.get(position);

        if (userAnswer.equals("Skipped")) {
            holder.questionNumber.setBackgroundColor(Color.parseColor("#FFEB3B"));
        } else if (userAnswer.equals("N/A")) {
            holder.questionNumber.setBackgroundColor(Color.BLACK);
        } else {
            holder.questionNumber.setBackgroundColor(Color.parseColor("#4CAF50"));
        }

        holder.questionNumber.setText("Q" + (position + 1));
    }


    @Override
    public int getItemCount() {
        return questionCount;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            questionNumber = itemView.findViewById(R.id.questionIndicator);
        }
    }
}
