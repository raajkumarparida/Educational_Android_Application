package com.sansan.vikashtutorial.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sansan.vikashtutorial.Mock.MockItem;
import com.sansan.vikashtutorial.R;

import java.util.List;

public class QuestionAnswerAdapter extends RecyclerView.Adapter<QuestionAnswerAdapter.ViewHolder> {

    private List<MockItem.QuestionModel> questionModelList;
    private List<String> userAnswers;

    public QuestionAnswerAdapter(List<MockItem.QuestionModel> questionModelList, List<String> userAnswers) {
        this.questionModelList = questionModelList;
        this.userAnswers = userAnswers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question_answer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MockItem.QuestionModel questionModel = questionModelList.get(position);
        holder.questionTextView.setText("Q" + (position + 1) + ": " + questionModel.getQuestion());
        holder.answerTextView.setText("A: " + questionModel.getCorrect());

        String userAnswer = (position < userAnswers.size()) ? userAnswers.get(position) : "N/A";

        holder.userAnswerTextView.setText("YA: " + userAnswer);

        if (userAnswer.equals("N/A")) {
            holder.userAnswerTextView.setTextColor(Color.parseColor("#BDBDBD"));
        } else if (userAnswer.equals(questionModel.getCorrect())) {
            holder.userAnswerTextView.setTextColor(Color.parseColor("#4CAF50"));
        } else if (userAnswer.equals("Skipped")) {
            holder.userAnswerTextView.setTextColor(Color.parseColor("#81D4FA"));
        } else {  
            holder.userAnswerTextView.setTextColor(Color.parseColor("#F44336"));
        }
    }

    @Override
    public int getItemCount() {
        return questionModelList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView, answerTextView, userAnswerTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.questionText);
            answerTextView = itemView.findViewById(R.id.correctAnswerText);
            userAnswerTextView = itemView.findViewById(R.id.userAnswerText);
        }
    }
}