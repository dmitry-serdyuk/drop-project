package com.dmitry.drop.project.presenter;

/**
 * Created by Laptop on 15/06/2016.
 */
public interface ViewPostPresenter {

    void loadReplies();

    void onSendReplyClick(long postId, String author, String annotation, String date, String imageFilePath);

    void onSelectImageClick();
}
