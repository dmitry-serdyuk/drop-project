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
 * Created by Dmitry on 16/06/2016.
 */
public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {

    List<Reply> replies;
    Context context;
    OnItemClickListener mItemClickListener;

    public ReplyAdapter(List<Reply> replies, Context context) {
        this.replies = replies;
        this.context = context;
    }

    @Override
    public ReplyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reply_item_layout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String imageFilePath = replies.get(position).getImageFilePath();
        String elapsedTime = getReplyTimeSpan(replies.get(position).getDateCreated());

        holder.authorTextView.setText(context.getString(R.string.username_placeholder));
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

    @Override
    public int getItemCount() {
        return replies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView authorTextView, commentTextView, dateTextView;
        public ImageView replyImage;

        public ViewHolder(View v) {
            super(v);
            authorTextView = ButterKnife.findById(v, R.id.replyLayout_author);
            commentTextView = ButterKnife.findById(v, R.id.replyLayout_comment);
            dateTextView = ButterKnife.findById(v, R.id.replyLayout_dateTimeCreated);
            replyImage = ButterKnife.findById(v, R.id.replyLayout_replyImage);
            replyImage.setOnClickListener(this);
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
