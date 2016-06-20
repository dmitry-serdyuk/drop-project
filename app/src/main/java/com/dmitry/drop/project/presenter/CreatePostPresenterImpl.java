package com.dmitry.drop.project.presenter;

import com.dmitry.drop.project.model.PostModelImpl;
import com.dmitry.drop.project.view.CreatePostView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

/**
 * Created by Laptop on 7/06/2016.
 */
public class CreatePostPresenterImpl extends MvpBasePresenter<CreatePostView> implements CreatePostPresenter {


    //TODO: SavePost in model

    @Override
    public void onDropButtonClick() {
        if (isViewAttached()) {
            getView().savePost();
            getView().returnToWorldMap();
        }


    }
}
