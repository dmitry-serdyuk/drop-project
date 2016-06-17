package com.dmitry.drop.project.presenter;


import com.dmitry.drop.project.view.WorldMapView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

/**
 * Created by Dima on 20/05/2016.
 */
public class WorldMapPresenterImpl extends MvpBasePresenter<WorldMapView> implements WorldMapPresenter {

    @Override
    public void onMyLocationClicked() {
        if (isViewAttached())
            getView().moveCameraToMyLocation();
    }

    @Override
    public void onAddPostClick() {
        if (isViewAttached())
            getView().addPost();
    }
}
