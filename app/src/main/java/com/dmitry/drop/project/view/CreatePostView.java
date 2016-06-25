package com.dmitry.drop.project.view;

import com.dmitry.drop.project.model.Post;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by Laptop on 7/06/2016.
 */
public interface CreatePostView extends MvpView {

    String POST_EXTRA = "post";
    String LATITUDE_EXTRA = "latitude";
    String LONGITUDE_EXTRA = "longitude";

    void onDropButtonClick();

    void returnToWorldMap(Post post);
}
