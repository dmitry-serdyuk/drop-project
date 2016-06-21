package com.dmitry.drop.project.presenter;

import com.dmitry.drop.project.view.ViewPostView;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;

/**
 * Created by Laptop on 15/06/2016.
 */
public interface ViewPostPresenter extends MvpPresenter<ViewPostView> {

    void loadReplies(long postId);

    void onSendReplyClick(long postId, String author, String annotation, String date, String imageFilePath);

    void onSelectImageClick();
}
