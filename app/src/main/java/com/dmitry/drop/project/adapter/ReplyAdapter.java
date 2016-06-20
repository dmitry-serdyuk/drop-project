package com.dmitry.drop.project.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dmitry.drop.project.R;
import com.dmitry.drop.project.model.Reply;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Laptop on 16/06/2016.
 */
public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {

    List<Reply> replies;
    Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public TextView mAuthorTextView, mCommentTextView, mDateTextView;
        public ImageView mReplyImage;

        public ViewHolder(View v) {
            super(v);
            mAuthorTextView = ButterKnife.findById(v, R.id.replyLayout_author);
            mAuthorTextView = ButterKnife.findById(v, R.id.replyLayout_author);
            mCommentTextView = ButterKnife.findById(v, R.id.replyLayout_comment);
            mDateTextView = ButterKnife.findById(v, R.id.replyLayout_dateTimeCreated);
            mReplyImage = ButterKnife.findById(v, R.id.replyLayout_replyImage);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReplyAdapter(List<Reply> replies, Context context) {
        this.replies = replies;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReplyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reply_layout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        String imageFilePath = replies.get(position).getImageFilePath();

        holder.mAuthorTextView.setText(replies.get(position).getAuthor());
        holder.mCommentTextView.setText(replies.get(position).getComment());
        holder.mDateTextView.setText(replies.get(position).getDateCreated());

        if (imageFilePath != null) {
            Bitmap replyImage = BitmapFactory.decodeFile(imageFilePath);
            holder.mReplyImage.setImageBitmap(replyImage);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return replies.size();
    }
}
