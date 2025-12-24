package com.sansan.vikashtutorial.Adapter;

import android.content.Context;
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
import com.bumptech.glide.request.target.Target;
import com.sansan.vikashtutorial.Item.FacultyItem;
import com.sansan.vikashtutorial.R;

import java.util.List;

public class FacultyImageAdapter extends RecyclerView.Adapter<FacultyImageAdapter.ImageViewHolder> {

    private Context context;
    private List<FacultyItem> facultyItems;

    public FacultyImageAdapter(Context context, List<FacultyItem> facultyItems) {
        this.context = context;
        this.facultyItems = facultyItems;
    }

    @NonNull
    @Override
    public FacultyImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.faculty_img_list, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacultyImageAdapter.ImageViewHolder holder, int position) {

        FacultyItem facultyItem = facultyItems.get(position);

        holder.name.setText(facultyItem.getName());
        holder.sub.setText(facultyItem.getSub());
        holder.qua.setText(facultyItem.getQua());

        Glide.with(context)
                .load(facultyItem.getImg())
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
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return facultyItems.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView sub;
        private TextView qua;
        private ImageView img;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.facName);
            name.setSelected(true);
            sub = itemView.findViewById(R.id.facSub);
            sub.setSelected(true);
            qua = itemView.findViewById(R.id.facQua);
            qua.setSelected(true);
            img = itemView.findViewById(R.id.facImg);
        }
    }
}
