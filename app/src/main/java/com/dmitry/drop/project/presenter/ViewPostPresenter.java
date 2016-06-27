package com.dmitry.drop.project.presenter;

import com.dmitry.drop.project.model.Post;
import com.dmitry.drop.project.view.ViewPostView;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;

/**
 * Created by Dmitry on 15/06/2016.
 */
public interface ViewPostPresenter extends MvpPresenter<ViewPostView> {

    void getReplies(Post post);

    void onSendReplyClick(Post post, String author, String annotation, String date, String imageFilePath);

    void onTakeReplyPhotoClick();

    void onStart(long postId);

    void onLikeClick();

    void onMainImgClick(String postImgFilePath);

    void onBackClick();

    void onDestroy(Post post, boolean liked);
}
