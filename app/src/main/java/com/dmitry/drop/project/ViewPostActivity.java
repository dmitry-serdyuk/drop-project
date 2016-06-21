package com.dmitry.drop.project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.dmitry.drop.project.adapter.ReplyAdapter;
import com.dmitry.drop.project.model.Post;
import com.dmitry.drop.project.model.PostModel;
import com.dmitry.drop.project.model.PostModelImpl;
import com.dmitry.drop.project.model.Reply;
import com.dmitry.drop.project.model.ReplyModel;
import com.dmitry.drop.project.model.ReplyModelImpl;
import com.dmitry.drop.project.presenter.ViewPostPresenter;
import com.dmitry.drop.project.presenter.ViewPostPresenterImpl;
import com.dmitry.drop.project.view.ViewPostView;
import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Laptop on 15/06/2016.
 */
public class ViewPostActivity extends MvpActivity<ViewPostView, ViewPostPresenter>
        implements ViewPostView {

    @BindView(R.id.viewPost_postImg)
    ImageView postImg;

    @BindView(R.id.viewPost_author)
    TextView postAuthor;

    @BindView(R.id.viewPost_annotation)
    TextView postAnnotation;

    @BindView(R.id.viewPost_timeElapsed)
    TextView postDateTime;

    @BindView(R.id.replyBar_comment)
    TextView replyComment;

    @BindView(R.id.viewPost_repliesRecyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.viewPost_swipeRefreshLayout)
    SwipyRefreshLayout mRefreshLayout;

    @BindView(R.id.replyBar_imgSelectButton)
    ImageView replyImage;

    private static final int REQUEST_CAMERA_PHOTO = 1;

    PostModel postModel;
    ReplyModel replyModel;
    private Bitmap mPostImg;
    private RecyclerView.Adapter mAdapter;
    private Post mPost;
    private String mImagePostFilePath;
    private String mImageReplyFilePath;
    private List<Reply> mReplies;
    private long mPostId;
    private boolean mInitialised;


    public static Intent createIntent(Context context, long postId) {
        Intent intent = new Intent(context, ViewPostActivity.class);
        intent.putExtra("postId", postId);
        return intent;
    }

    @NonNull
    @Override
    public ViewPostPresenter createPresenter() {
        postModel = new PostModelImpl();
        replyModel = new ReplyModelImpl();
        return new ViewPostPresenterImpl(postModel, replyModel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        ButterKnife.bind(this);

        mInitialised = false;
        replyImage.setAdjustViewBounds(true);
        Intent viewPost = getIntent();
        mPostId = viewPost.getLongExtra("postId", -1);
        mPost = postModel.getPost(mPostId);
        mImagePostFilePath = mPost.getImageFilePath();


        mPostImg = BitmapFactory.decodeFile(mImagePostFilePath);
        postImg.setImageBitmap(mPostImg);

        postAnnotation.setText(mPost.getAnnotation());
        postDateTime.setText(getPostTimeSpan());

        //load post replies from model
        presenter.loadReplies(mPostId);


        mRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                presenter.loadReplies(mPostId);
            }
        });


    }

    private String getPostTimeSpan() {
        String string_date = mPost.getDateCreated();
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyy HH:mm", Locale.US);
        Date d = null;
        try {
            d = f.parse(string_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long dateCreatedMilleseconds = 0;
        if (d != null) {
            dateCreatedMilleseconds = d.getTime();
        }
        return DateUtils.getRelativeTimeSpanString(dateCreatedMilleseconds, new Date().getTime(), DateUtils.MINUTE_IN_MILLIS).toString();

    }


    // TODO: Move these onclick methods outside of interface

    @OnClick(R.id.replyBar_sendButton)
    public void onSendReplyClick() {
        String author = getString(R.string.username_placeholder);
        String annotation = replyComment.getText().toString();
        String date = DateFormat.getInstance().format(new Date());

        mRefreshLayout.setRefreshing(true);
        presenter.onSendReplyClick(mPost.getId(), author, annotation, date, mImageReplyFilePath);

    }

    private void closeSoftKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void showReplyLoadingError() {
        Toast.makeText(this, getString(R.string.reply_loading_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void clearReplyBox() {
        closeSoftKeyboard();
        mImageReplyFilePath = null;
        replyComment.setText("");

        replyImage.setImageDrawable(getDrawable(android.R.drawable.ic_menu_camera));
    }


    // TODO: Change signature to showReplies(List<Post>)
    @Override
    public void showReplies(List<Reply> replies) {

        //if activity is started, initialise recycler view and adapter
        if (!mInitialised) {
            mInitialised = true;
            mReplies = replies;
            mAdapter = new ReplyAdapter(mReplies, this);

            mRecyclerView.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(this));


            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        } else {
            mRefreshLayout.setRefreshing(false);
            mReplies.clear();
            mReplies.addAll(replies);
            mAdapter.notifyDataSetChanged();
            mRecyclerView.invalidate();

        }
        if (replies.size() == 0)
            mRecyclerView.smoothScrollToPosition(mReplies.size());
        else
            mRecyclerView.smoothScrollToPosition(mReplies.size() - 1);
    }

    @Override
    @OnClick(R.id.replyBar_imgSelectButton)
    public void onSelectImageClick() {
        presenter.onSelectImageClick();
    }

    @Override
    public void selectImage() {
        Intent takePhoto = new Intent(this, CameraActivity.class);
        startActivityForResult(takePhoto, REQUEST_CAMERA_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA_PHOTO && resultCode == RESULT_OK) {
            mImageReplyFilePath = data.getStringExtra("imageFilePath");

            setReplyThumbnail();
        } else if (requestCode == REQUEST_CAMERA_PHOTO && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, getString(R.string.photo_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void setReplyThumbnail() {
        Bitmap bitmap = BitmapFactory.decodeFile(mImageReplyFilePath);
        replyImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 50, 50, false));
    }

    private class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = getDrawable(R.drawable.recycler_view_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    private class LinearLayoutManagerWithSmoothScroller extends LinearLayoutManager {

        public LinearLayoutManagerWithSmoothScroller(Context context) {
            super(context, VERTICAL, false);
        }

        public LinearLayoutManagerWithSmoothScroller(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
                                           int position) {
            RecyclerView.SmoothScroller smoothScroller = new TopSnappedSmoothScroller(recyclerView.getContext());
            smoothScroller.setTargetPosition(position);
            startSmoothScroll(smoothScroller);
        }

        private class TopSnappedSmoothScroller extends LinearSmoothScroller {
            public TopSnappedSmoothScroller(Context context) {
                super(context);

            }

            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return LinearLayoutManagerWithSmoothScroller.this
                        .computeScrollVectorForPosition(targetPosition);
            }

            @Override
            protected int getVerticalSnapPreference() {
                return SNAP_TO_START;
            }
        }
    }
}
