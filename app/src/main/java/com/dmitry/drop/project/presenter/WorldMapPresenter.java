package com.dmitry.drop.project.presenter;

import com.dmitry.drop.project.model.Post;

import java.util.List;

/**
 * Created by Laptop on 22/05/2016.
 */
public interface WorldMapPresenter {

    void onMyLocationClicked();

    void onAddPostClick();

    void onMapClicked(List<Post> posts, double latitude, double longitude);

}
