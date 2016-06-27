package com.dmitry.drop.project.view;

import com.dmitry.drop.project.model.Post;
import com.dmitry.drop.project.model.Reply;
import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

/**
 * Created by Laptop on 15/06/2016.
 */
public interface ViewPostView extends MvpView {

    String POST_ID_EXTRA = "postId";
    String CAN_REPLY_EXTRA = "canReply";

    int REPLY_IMG_WIDTH = 50;
    int REPLY_IMG_HEIGHT = 50;

    void showReplies(List<Reply> replies);

    void takeReplyPicture();

    void showReplyLoadingError(String error);

    void showPostLoadingError(String error);

    void clearReplyBox();

    void showPost(Post post);

    void showRepliesLoading(boolean loading);

    void showSendReplyError(String error);

    void showLikeAnim();

    void showFullscreenImg(String imageFilePath);

    void hideFullscreenImg();
}
