package com.dmitry.drop.project.presenter;

import com.dmitry.drop.project.view.CreatePostView;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;

/**
 * Created by Laptop on 7/06/2016.
 */
public interface CreatePostPresenter  extends MvpPresenter<CreatePostView> {

    void onDropButtonClick();
}
