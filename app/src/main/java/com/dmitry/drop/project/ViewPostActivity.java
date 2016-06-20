package com.dmitry.drop.project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.activeandroid.query.Select;
import com.dmitry.drop.project.adapter.ReplyAdapter;
import com.dmitry.drop.project.model.Post;
import com.dmitry.drop.project.model.Reply;
import com.dmitry.drop.project.presenter.ViewPostPresenterImpl;
import com.dmitry.drop.project.view.ViewPostView;
import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Laptop on 15/06/2016.
 */
public class ViewPostActivity extends MvpActivity<ViewPostView, ViewPostPresenterImpl>
        implements ViewPostView {

    @BindView(R.id.viewPost_postImg)
    ImageView postImg;

    @BindView(R.id.viewPost_author)
    TextView postAuthor;

    @BindView(R.id.viewPost_annotation)
    TextView postAnnotation;

    @BindView(R.id.viewPost_dateTimeCreated)
    TextView postDateTime;

    @BindView(R.id.replyBar_comment)
    TextView replyComment;

    @BindView(R.id.viewPost_repliesRecyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.viewPost_swipeRefreshLayout)
    SwipyRefreshLayout mRefreshLayout;

    private static final int REQUEST_CAMERA_PHOTO = 1;

    private Bitmap mPostImg;
    private RecyclerView.Adapter mAdapter;
    private Post mPost;
    private String mImagePostFilePath;
    private String mImageReplyFilePath;
    private List<Reply> mReplies;
    private long mPostId;


    public static Intent createIntent(Context context, long postId) {
        Intent intent = new Intent(context, ViewPostActivity.class);
        intent.putExtra("postId", postId);
        return intent;
    }

    @NonNull
    @Override
    public ViewPostPresenterImpl createPresenter() {
        return new ViewPostPresenterImpl();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        ButterKnife.bind(this);

        Intent viewPost = getIntent();
        mPostId = viewPost.getLongExtra("postId", -1);
        mReplies = getReplies(mPostId);
        mImagePostFilePath = mPost.getImageFilePath();


        mPostImg = BitmapFactory.decodeFile(mImagePostFilePath);
        postImg.setImageBitmap(mPostImg);

        postAnnotation.setText(mPost.getAnnotation());
        postDateTime.setText(mPost.getDateCreated());

        //Get post replies


        //Display post replies
        mAdapter = new ReplyAdapter(mReplies, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                presenter.loadReplies();
            }
        });
    }


    // TODO: Move to model
    @Override
    public List<Reply> getReplies(long postId) {
        if (postId != -1) {
            mPost = new Select().from(Post.class).where("id = ?", postId).executeSingle();
            return mPost.replies();
        }
        return Collections.emptyList();
    }


    // TODO: Move these onclick methods outside of interface

    @OnClick(R.id.replyBar_sendButton)
    public void onSendReplyClick() {
        closeSoftKeyboard();

        String author = getString(R.string.username_placeholder);
        String annotation = replyComment.getText().toString();
        String date = DateFormat.getInstance().format(new Date());
        replyComment.setText("");

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

    // TODO: Change signature to setReplies(List<Post>)
    @Override
    public void refreshReplies() {
        mImageReplyFilePath = null;
        mRefreshLayout.setRefreshing(false);

        mReplies.clear();
        mReplies.addAll(getReplies(mPostId));
        mAdapter.notifyDataSetChanged();
        mRecyclerView.invalidate();
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

        } else if (requestCode == REQUEST_CAMERA_PHOTO && resultCode == RESULT_CANCELED) {
            // TODO: Use string values
            Toast.makeText(this, getString(R.string.photo_error), Toast.LENGTH_SHORT).show();
        }
    }
}
