package com.dmitry.drop.project.view;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by Laptop on 7/06/2016.
 */
public interface CreatePostView extends MvpView {

    void onDropButtonClick();

    void savePost();
}
