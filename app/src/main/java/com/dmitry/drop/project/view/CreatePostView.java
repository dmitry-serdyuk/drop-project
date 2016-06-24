package com.dmitry.drop.project.view;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by Laptop on 7/06/2016.
 */
public interface CreatePostView extends MvpView {

    String POST_ID_EXTRA = "IdExtra";

    void onDropButtonClick();

    void returnToWorldMap(long postId);
}
