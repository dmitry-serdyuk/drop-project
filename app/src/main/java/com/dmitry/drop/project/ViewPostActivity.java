package com.dmitry.drop.project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dmitry.drop.project.utility.AnimUtils.getFastOutSlowInInterpolator;

/**
 * Created by Laptop on 15/06/2016.
 */
public class ViewPostActivity extends MvpActivity<ViewPostView, ViewPostPresenter>
        implements ViewPostView {

    private static final int REQUEST_CAMERA_PHOTO = 1;
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
    @BindView(R.id.viewPost_postImgOverlay)
    ImageView postImageOverlay;
    @BindView(R.id.viewPost_accountLayout)
    RelativeLayout viewPostAccountLayout;
    @BindView(R.id.viewPost_layout)
    RelativeLayout viewPostLayout;
    @BindView(R.id.viewPost_heading)
    RelativeLayout viewPostHeading;
    @BindView(R.id.viewPost_postImgFrame)
    RelativeLayout viewPostImgFrame;
    @BindView(R.id.viewPost_repliesPlaceholder)
    RelativeLayout repliesPlaceholder;
    @BindView(R.id.viewPost_barLayout)
    LinearLayout barLayout;
    @BindView(R.id.viewPost_repliesLoading)
    ProgressBar repliesLoading;
    @BindView(R.id.viePost_noRepliesPlaceholder)
    TextView noRepliesPlaceholder;
    // TODO: See if you can remove any duplicate values
    PostModel postModel;
    ReplyModel replyModel;
    private RecyclerView.Adapter mAdapter;
    private Post mPost;
    private String mImagePostFilePath;
    private String mImageReplyFilePath;
    private List<Reply> mReplies;
    private long mPostId;
    private boolean mInitialised;

    // TODO: Pass post in so you won't have to bother with getting post
    public static Intent createIntent(Context context, long postId) {
        Intent intent = new Intent(context, ViewPostActivity.class);
        // TODO: PutExtra constants :p
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

    // TODO: Separate this function into your first 3 methods, the setupView(), then showPost(post), then presenter.loadReplies()

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

        barLayout.setAlpha(0f);

        Glide.with(this).load(mImagePostFilePath).into(postImg);
        Glide.with(this).load("").placeholder(getDrawable(R.drawable.post_img_overlay)).into(postImageOverlay);

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

        viewPostLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver
                .OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                viewPostLayout.getViewTreeObserver().removeOnPreDrawListener(this);

                enterAnimation();
                return true;
            }
        });
    }

    private void enterAnimation() {

        Interpolator interp = getFastOutSlowInInterpolator(this);

        int offset = viewPostHeading.getHeight();
        viewEnterAnimation(viewPostHeading, offset, interp);

        offset *= 1.5f;
        viewEnterAnimation(postImg, offset, interp);

        offset *= 1.5f;
        viewEnterAnimation(mRefreshLayout, offset, interp);

        enterAcountFrameAnimation(interp);

        offset *= 1.0f;
        viewEnterAnimation(barLayout, offset, interp);
    }

    private void enterAcountFrameAnimation(Interpolator interp) {
        viewPostAccountLayout.setAlpha(0.0f);
        viewPostAccountLayout.animate()
                .alpha(1f)
                .setStartDelay(500)
                .setDuration(3000)
                .setInterpolator(interp)
                .setListener(null)
                .start();
    }

    private void viewEnterAnimation(View view, float offset, Interpolator interp) {
        view.setTranslationY(offset);
        view.setAlpha(0.0f);
        view.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setInterpolator(interp)
                .setListener(null)
                .start();
    }

    private String getPostTimeSpan() {
        // TODO: Naming conventions
        String string_date = mPost.getDateCreated();
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyy HH:mm", Locale.US);
        Date d;

        try {
            d = f.parse(string_date);
            long dateCreatedMilleseconds = d.getTime();
            Toast.makeText(this, "Time = " + dateCreatedMilleseconds, Toast.LENGTH_SHORT).show();

            // TODO: rename string s and use string resources
            String s = DateUtils.getRelativeTimeSpanString(dateCreatedMilleseconds, new Date().getTime(), DateUtils.MINUTE_IN_MILLIS).toString();
            //if less than a minute show "just now"
            if (s.equals("0 minutes ago"))
                return "Just now";

            return s;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

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

    @Override
    public void showRepliesLoading(boolean loading) {
        if (loading)
            repliesLoading.setVisibility(View.VISIBLE);
        else
            repliesLoading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showReplies(List<Reply> replies) {

        // TODO: Move to OnCreate
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

        // TODO: Add empty state here
        if (replies.size() == 0) {
            mRecyclerView.smoothScrollToPosition(mReplies.size());
            noRepliesPlaceholder.setVisibility(View.VISIBLE);

        } else {
            mRecyclerView.smoothScrollToPosition(mReplies.size() - 1);
            noRepliesPlaceholder.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.replyBar_imgSelectButton)
    public void onSelectImageClick() {
        presenter.onSelectImageClick();
    }

    @Override
    public void takeReplyPicture() {
        // TODO: Create intent
        Intent takePhoto = new Intent(this, CameraActivity.class);
        startActivityForResult(takePhoto, REQUEST_CAMERA_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA_PHOTO && resultCode == RESULT_OK) {
            // TODO: PutExtra Constants
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

    //Recycler View classes
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
