package com.dmitry.drop.project.presenter;


import com.dmitry.drop.project.model.Post;
import com.dmitry.drop.project.view.WorldMapView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.List;

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

    @Override
    public void onMapClicked(List<Post> posts, double latitude, double longitude) {
        boolean clicked = false;
        Post clickedPost = new Post();

        for (Post post : posts) {

            if (post.isWithinRadius(latitude, longitude)) {
                clickedPost = post;
                clicked = true;
                break;
            }
        }

        if (clicked) {
            if (isViewAttached())
                getView().viewPost(clickedPost);
        }
    }
}
