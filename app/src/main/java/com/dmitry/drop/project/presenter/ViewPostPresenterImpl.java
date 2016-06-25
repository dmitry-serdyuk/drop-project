package com.dmitry.drop.project.presenter;

import com.dmitry.drop.project.model.Post;
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

    public ViewPostPresenterImpl(PostModel postModel) {
        this.postModel = postModel;
    }

    @Override
    public void getReplies(Post post) {
        if (isViewAttached()) {
            getView().showRepliesLoading(true);
        }
        postModel.getReplies(post, new PostModel.GetRepliesCallback() {
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
    public void onSendReplyClick(final Post post, String author, String annotation, String date, String imageFilePath) {
        postModel.saveReply(post, author, annotation, date, imageFilePath, new PostModel.SaveReplyCallback() {
            @Override
            public void onSuccess() {
                getReplies(post);
                clearReplyBox();
            }

            @Override
            public void onError(String error) {
                if (isViewAttached()) {
                    getView().showSendReplyError(error);
                }
            }
        });
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
