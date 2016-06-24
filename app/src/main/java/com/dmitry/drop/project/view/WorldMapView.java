package com.dmitry.drop.project.view;

import com.dmitry.drop.project.model.Post;
import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

/**
 * Created by Laptop on 22/05/2016.
 */
public interface WorldMapView extends MvpView {

    void moveCameraToMyLocation();

    void createPost();

    void viewPost(Post post);

    void showPosts(List<Post> posts);

    void addPost(Post post);
}
