package com.dmitry.drop.project.presenter;

import com.dmitry.drop.project.model.PostModel;
import com.dmitry.drop.project.model.Reply;
import com.dmitry.drop.project.model.ReplyModel;
import com.dmitry.drop.project.view.ViewPostView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.List;

/**
 * Created by Laptop on 15/06/2016.
 */
public class ViewPostPresenterImpl extends MvpBasePresenter<ViewPostView> implements ViewPostPresenter {

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
        if (isViewAttached()) {
            getView().showRepliesLoading(true);
        }
        postModel.getReplies(postId, new PostModel.GetRepliesCallback() {
            @Override
            public void onSuccess(List<Reply> replies) {
                if (isViewAttached()) {
                    getView().showRepliesLoading(false);
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
        // Show loading spinner

        // Do post model thing

        // In success and error of callback, first thing you do is dismiss loading spinner

        // Show saveReplyLoading instead of reusing replies loading

        // TODO: Use CALLBACK with model
        if (postId != -1) {
            // Implement this signature postModel.saveReply(postId, author, annotation, date, imageFilePath);
            postModel.saveReply(postId, author, annotation, date, imageFilePath);
        }

        // TODO: Should be done after onSuccess of savingReply
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
            getView().takeReplyPicture();
    }

}
