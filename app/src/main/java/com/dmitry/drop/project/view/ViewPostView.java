package com.dmitry.drop.project.view;

import com.dmitry.drop.project.model.Reply;
import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

/**
 * Created by Laptop on 15/06/2016.
 */
public interface ViewPostView extends MvpView {

    String POST_EXTRA = "post";

    int REPLY_IMG_WIDTH = 50;
    int REPLY_IMG_HEIGHT = 50;

    void onSendReplyClick();

    void showReplies(List<Reply> replies);

    void takeReplyPicture();

    void showReplyLoadingError();

    void clearReplyBox();

    void showRepliesLoading(boolean loading);

    void showSendReplyError(String error);
}
