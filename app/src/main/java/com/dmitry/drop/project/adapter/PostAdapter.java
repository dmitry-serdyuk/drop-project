package com.dmitry.drop.project.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.dmitry.drop.project.R;
import com.dmitry.drop.project.model.Post;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Dima on 25/06/2016.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    List<Post> posts;
    Context context;
    double latitude;
    double longitude;
    OnItemClickListener mItemClickListener;

    public PostAdapter(List<Post> posts, Context context, double latitude, double longitude) {
        this.posts = posts;
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item_layout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void reset() {
        posts.clear();
        this.notifyDataSetChanged();
    }

    public void add(Post post) {
        posts.add(post);
        notifyItemInserted(posts.size()-1);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String imageFilePath = posts.get(position).getImageFilePath();
        Glide.with(context).load(imageFilePath).centerCrop().into(holder.postImage);

        //glide resources in to match with adapter item animation
        Glide.with(context).load("").placeholder(
                context.getDrawable(R.drawable.post_img_overlay)).into(holder.imageOverlay);

        if (posts.get(position).isWithinRadius(latitude, longitude)) {
            Glide.with(context).load("").placeholder(
                    context.getDrawable(R.drawable.view_icon_activated)).into(holder.viewIcon);
        } else {
            Glide.with(context).load("").placeholder(
                    context.getDrawable(R.drawable.view_icon)).into(holder.viewIcon);
        }

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView postImage, imageOverlay, viewIcon;

        public ViewHolder(View v) {
            super(v);
            postImage = ButterKnife.findById(v, R.id.postItem_image);
            imageOverlay = ButterKnife.findById(v, R.id.postItem_postImgOverlay);
            viewIcon = ButterKnife.findById(v, R.id.postItem_viewIcon);
            postImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
