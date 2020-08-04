package com.example.newroutes.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newroutes.ParseObjects.Comment;
import com.example.newroutes.R;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context context;
    private List<Comment> comments;
    public static final String TAG = "CommentAdapter";

    public CommentAdapter(Context context, List<Comment> comments) {
        this.comments = comments;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment,parent,false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment newComment = comments.get(position);
        try {
            holder.bind(newComment);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        TextView tvUsername;
        TextView tvComment;
        ImageView ivProfilePicture;
        Comment comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvComment = itemView.findViewById(R.id.tvComment);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
        }

        public void bind(Comment newComment) throws ParseException {
            comment = newComment;
            ParseUser creator = comment.getCreator().fetchIfNeeded();
            tvUsername.setText(creator.getUsername());
            tvComment.setText(comment.getContent());
            Glide.with(context).load(creator.getParseFile("Picture").getUrl()).into(ivProfilePicture);
        }
    }
}
