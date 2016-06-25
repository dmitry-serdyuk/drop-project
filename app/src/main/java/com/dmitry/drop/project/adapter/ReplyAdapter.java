package com.dmitry.drop.project.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dmitry.drop.project.R;
import com.dmitry.drop.project.model.Reply;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;

/**
 * Created by Laptop on 16/06/2016.
 */
public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {

    List<Reply> replies;
    Context context;

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
                .inflate(R.layout.post_item_layout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        String imageFilePath = replies.get(position).getImageFilePath();
        String elapsedTime = getReplyTimeSpan(replies.get(position).getDateCreated());

        holder.authorTextView.setText(replies.get(position).getAuthor());
        holder.commentTextView.setText(replies.get(position).getComment());
        holder.dateTextView.setText(elapsedTime);

        if (imageFilePath != null) {
            Glide.with(context).load(imageFilePath).centerCrop().into(holder.replyImage);
        }
    }

    private String getReplyTimeSpan(String dateCreated) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyy HH:mm", Locale.US);
        Date parsedDate;
        String timeSpan = null;

        try {
            parsedDate = format.parse(dateCreated);
            long dateCreatedMilliseconds = parsedDate.getTime();

            timeSpan = DateUtils.getRelativeTimeSpanString(dateCreatedMilliseconds, new Date().getTime(), DateUtils.MINUTE_IN_MILLIS).toString();
            //if less than a minute show "just now"
            if (timeSpan.equals(context.getString(R.string.zero_minutes_ago_text)))
                return context.getString(R.string.just_now_text);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeSpan;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return replies.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public TextView authorTextView, commentTextView, dateTextView;
        public ImageView replyImage;

        public ViewHolder(View v) {
            super(v);
            authorTextView = ButterKnife.findById(v, R.id.replyLayout_author);
            authorTextView = ButterKnife.findById(v, R.id.replyLayout_author);
            commentTextView = ButterKnife.findById(v, R.id.replyLayout_comment);
            dateTextView = ButterKnife.findById(v, R.id.replyLayout_dateTimeCreated);
            replyImage = ButterKnife.findById(v, R.id.replyLayout_replyImage);
        }
    }
}
