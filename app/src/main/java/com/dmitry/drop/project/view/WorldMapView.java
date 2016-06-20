package com.dmitry.drop.project.view;

import com.dmitry.drop.project.model.Post;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by Laptop on 22/05/2016.
 */
public interface WorldMapView extends MvpView {

    void moveCameraToMyLocation();

    void addPost();

    void viewPost(Post post);


}
