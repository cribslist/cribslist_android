package com.codepath.cribslist.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.cribslist.R;
import com.codepath.cribslist.activities.ItemDetail;
import com.codepath.cribslist.models.Item;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private ArrayList<Item> mDataset;
    private final Boolean isOwnListings;
    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View mItemView;
        ImageView mThumbnail;
        TextView mTitle;
        private Context context;
        public ItemViewHolder(Context context, View v) {
            super(v);
            mItemView = v;
            this.context = context;
            v.setOnClickListener( ItemViewHolder.this);
            setUpViews();
        }
        private void setUpViews(){
            mThumbnail = mItemView.findViewById(R.id.thumbnail);
            mTitle = mItemView.findViewById(R.id.title);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Item item = mDataset.get(position);
                Intent i = new Intent(context, ItemDetail.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity)context,(View)mThumbnail, "main");
                i.putExtra("item", item);
                i.putExtra("isOwnListing", isOwnListings);
                context.startActivity(i, options.toBundle());
            }
        }
    }


    public ItemAdapter(ArrayList<Item> myDataset, Boolean isOwnListings) {
        this.mDataset = myDataset;
        this.isOwnListings = isOwnListings;
    }

    @Override
    public ItemViewHolder onCreateViewHolder( ViewGroup parent,
                                             int viewType) {
        Context ctx = parent.getContext();
        View v = LayoutInflater.from(ctx)
                .inflate(R.layout.item_view, parent, false);

        ItemViewHolder vh = new ItemViewHolder(ctx, v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_stroller_24dp)
                .error(R.drawable.ic_stroller_24dp);
        Item item = mDataset.get(position);
        Glide.with(holder.mThumbnail.getContext())
                .load(item.getThumbnailURL())
                .transition(withCrossFade())
                .apply(options)
                .into(holder.mThumbnail);
        holder.mTitle.setText(item.getTitle());

    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }



}