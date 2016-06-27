package com.dmitry.drop.project.presenter;

import com.dmitry.drop.project.model.Post;
import com.dmitry.drop.project.model.PostModel;
import com.dmitry.drop.project.model.Reply;
import com.dmitry.drop.project.view.ViewPostView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.List;

/**
 * Created by Dmitry on 15/06/2016.
 *
 * Presenter implementations handle view events such as onStart or onClick
 * The presenter interacts with a data model to retrieve data through callbacks
 * This data is then passed to the view along with any loading or error calls
 *
 * The model instance is obtain from an activity
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
                    getView().showReplyLoadingError(error);
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
    public void onTakeReplyPhotoClick() {
        if (isViewAttached())
            getView().takeReplyPicture();
    }

    @Override
    public void onStart(long postId) {
        postModel.getPost(postId, new PostModel.GetPostCallback() {
            @Override
            public void onSuccess(Post post) {
                if (isViewAttached()) {
                    getView().showPost(post);
                    getReplies(post);
                }
            }

            @Override
            public void onError(String error) {
                if (isViewAttached())
                    getView().showPostLoadingError(error);
            }
        });
    }

    @Override
    public void onLikeClick() {
        if (isViewAttached())
            getView().showLikeAnim();
    }

    @Override
    public void onMainImgClick(String postImgFilePath) {
        if (isViewAttached())
            getView().showFullscreenImg(postImgFilePath);
    }

    @Override
    public void onBackClick() {
        if (isViewAttached())
            getView().hideFullscreenImg();
    }

    @Override
    public void onDestroy(Post post, boolean liked) {
        postModel.like(post, liked);
    }

}
