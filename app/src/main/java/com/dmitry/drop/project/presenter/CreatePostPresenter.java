package com.dmitry.drop.project.presenter;

import com.dmitry.drop.project.view.CreatePostView;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;

/**
 * Created by Dmitry on 7/06/2016.
 *
 * Mosby MVP presenter interface
 * Methods that will be used by the CreatePostPresenter are declared here
 */
public interface CreatePostPresenter extends MvpPresenter<CreatePostView> {

    void onDropButtonClick(String annotationText, String cameraImageFilePath,
                           String thumbnailImageFilePath, double latitude,
                           double longitude, String date);
}
