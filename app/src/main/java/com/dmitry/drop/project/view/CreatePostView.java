package com.dmitry.drop.project.view;

import com.dmitry.drop.project.model.Post;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by Dmitry on 7/06/2016.
 *
 * View interfaces are implemented by activities and used by the presenters to interact with the view
 */
public interface CreatePostView extends MvpView {

    String POST_EXTRA = "post";
    String LATITUDE_EXTRA = "latitude";
    String LONGITUDE_EXTRA = "longitude";

    void showSavePostError(String error);

    void returnToWorldMap(Post post);
}
