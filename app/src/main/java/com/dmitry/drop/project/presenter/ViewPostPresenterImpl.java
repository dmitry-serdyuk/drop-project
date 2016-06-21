package com.dmitry.drop.project.presenter;

import android.text.format.DateUtils;

import com.activeandroid.query.Select;
import com.dmitry.drop.project.model.Post;
import com.dmitry.drop.project.model.PostModel;
import com.dmitry.drop.project.model.RepliesAsyncLoader;
import com.dmitry.drop.project.model.Reply;
import com.dmitry.drop.project.model.ReplyModel;
import com.dmitry.drop.project.view.ViewPostView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Laptop on 15/06/2016.
 */
public class ViewPostPresenterImpl extends MvpBasePresenter<ViewPostView> implements ViewPostPresenter {

    private RepliesAsyncLoader repliesLoader;
    private PostModel postModel;
    private ReplyModel replyModel;

    public ViewPostPresenterImpl(PostModel postModel, ReplyModel replyModel) {
        this.postModel = postModel;
        this.replyModel = replyModel;
    }

    @Override
    public void loadReplies(final long postId) {
        getReplies(postId);
    }

    private void getReplies(long postId) {
        postModel.getReplies(postId, new PostModel.GetRepliesCallback() {
            @Override
            public void onSuccess(List<Reply> replies) {
                if (isViewAttached()) {
                    getView().showReplies(replies);
                }
            }

            @Override
            public void onError(String error) {
                if (isViewAttached()) {
                    getView().showReplyLoadingError();
                }
            }
        });
    }

    @Override
    public void onSendReplyClick(long postId, String author, String annotation, String date, String imageFilePath) {
        // TODO: Use callback with model
        Post post;
        if (postId != -1) {
            post = postModel.getPost(postId);

            replyModel.saveReply(post, author, annotation, date, imageFilePath);
        }

        getReplies(postId);
        clearReplyBox();
    }

    private void clearReplyBox() {
        if (isViewAttached())
            getView().clearReplyBox();
    }

    @Override
    public void onSelectImageClick() {
        if (isViewAttached())
            getView().selectImage();
    }

}
