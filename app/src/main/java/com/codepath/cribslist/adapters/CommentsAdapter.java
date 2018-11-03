package com.codepath.cribslist.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.cribslist.R;
import com.codepath.cribslist.models.Comment;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {
    private ArrayList<Comment> mDataset;
    public class CommentsViewHolder extends RecyclerView.ViewHolder {
        View mItemView;
        ImageView mThumbnail;
        TextView mUsername;
        TextView mText;
        private Context context;
        public CommentsViewHolder(Context context, View v) {
            super(v);
            mItemView = v;
            this.context = context;
            setUpViews();
        }
        private void setUpViews(){
            mThumbnail = mItemView.findViewById(R.id.profPic);
            mUsername = mItemView.findViewById(R.id.name);
            mText = mItemView.findViewById(R.id.text);
        }

    }

    public CommentsAdapter(ArrayList<Comment> myDataset) { mDataset = myDataset; }

    @Override
    public CommentsViewHolder onCreateViewHolder( ViewGroup parent,
                                              int viewType) {
        Context ctx = parent.getContext();
        View v = LayoutInflater.from(ctx)
                .inflate(R.layout.comment_view, parent, false);

        CommentsViewHolder vh = new CommentsViewHolder(ctx, v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final CommentsViewHolder holder, int position) {
        Comment comment = mDataset.get(position);
        holder.mText.setText(comment.getText());
        holder.mUsername.setText(comment.getUsername());
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }



}