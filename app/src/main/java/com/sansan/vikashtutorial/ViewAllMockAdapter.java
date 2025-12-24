package com.sansan.vikashtutorial;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.sansan.vikashtutorial.Mock.MockActivity;
import com.sansan.vikashtutorial.Mock.MockItem;
import com.sansan.vikashtutorial.R;

import java.util.List;

public class ViewAllMockAdapter extends RecyclerView.Adapter<ViewAllMockAdapter.MyViewHolder> {
    private Context context;
    private List<MockItem> mockItemList;
    private ProgressDialog progressDialog;

    public ViewAllMockAdapter(Context context, List<MockItem> mockItemList) {
        this.context = context;
        this.mockItemList = mockItemList;
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_mock_viewall, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MockItem quiz = mockItemList.get(position);
        holder.quizTitleText.setText(quiz.getTitle());
        holder.quizSubtitleText.setText(quiz.getSubtitle());
        holder.quizTimeText.setText(quiz.getTime() + " min");

        Glide.with(context)
                .load(quiz.getImg())
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
                .into(holder.quizImageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MockActivity.class);
            intent.putExtra("quizModel", mockItemList.get(position));
            intent.putExtra("quizTitle", quiz.getTitle());
            intent.putExtra("quizSubject", quiz.getSubtitle());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mockItemList.size();
    }

    public void updateList(List<MockItem> newList) {
        mockItemList.clear();
        mockItemList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView quizTitleText;
        private final TextView quizSubtitleText;
        private final TextView quizTimeText;
        private final ImageView quizImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            quizTitleText = itemView.findViewById(R.id.quizTitle);
            quizTitleText.setSelected(true);
            quizSubtitleText = itemView.findViewById(R.id.quizSubject);
            quizSubtitleText.setSelected(true);
            quizTimeText = itemView.findViewById(R.id.quizTime);
            quizImageView = itemView.findViewById(R.id.quizImg);
        }
    }
}
