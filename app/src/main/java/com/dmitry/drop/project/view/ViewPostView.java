package com.dmitry.drop.project.view;

import com.dmitry.drop.project.model.Reply;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import java.util.List;

/**
 * Created by Laptop on 15/06/2016.
 */
public interface ViewPostView extends MvpView {

    void onSendReplyClick();

    void refreshReplies();

    void onSelectImageClick();

    void selectImage();

    List<Reply> getReplies(long postId);

}
