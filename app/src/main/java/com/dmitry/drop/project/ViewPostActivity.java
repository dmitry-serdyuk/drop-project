package com.dmitry.drop.project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.dmitry.drop.project.adapter.ReplyAdapter;
import com.dmitry.drop.project.model.Post;
import com.dmitry.drop.project.model.Reply;
import com.dmitry.drop.project.presenter.ViewPostPresenterImpl;
import com.dmitry.drop.project.view.ViewPostView;
import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceActivity;
import com.orm.SugarRecord;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    ImageView postImgView;

    @BindView(R.id.viewPost_author)
    TextView postAuthor;

    @BindView(R.id.viewPost_annotation)
    TextView annotation;

    @BindView(R.id.viewPost_repliesRecyclerView)
    RecyclerView mRecyclerView;

    private Bitmap mPostImg;

    Post post;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static Intent createIntent(Context context, String imgFilePath, String annotation, long postId) {
        Intent intent = new Intent(context, ViewPostActivity.class);
        intent.putExtra("imgFilePath", imgFilePath);
        intent.putExtra("annotation", annotation);
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

        String imgFilePath = getIntent().getStringExtra("imgFilePath");
        String annotationText = getIntent().getStringExtra("annotation");

        mPostImg = BitmapFactory.decodeFile(imgFilePath);
        postImgView.setImageBitmap(mPostImg);

        annotation.setText(annotationText);


        Intent intent = getIntent();
        List<Reply> replies = null;

        long id = intent.getLongExtra("postId", -1);

        if (id != -1) {
            post = Post.findById(Post.class, id);
            Reply reply = new Reply(post, "IamAuthor", "comment", "dateisNow", "null");
            reply.save();
            post.save();
            replies = Reply.listAll(Reply.class);
            if(replies != null)
                Toast.makeText(this, replies.size() + "", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "replies are null", Toast.LENGTH_SHORT).show();
        }




        mAdapter = new ReplyAdapter(replies);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    @OnClick(R.id.replyBar_sendButton)
    public void onSendReplyClick() {



    }
}
