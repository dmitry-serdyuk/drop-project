package com.dmitry.drop.project.view;

import android.animation.ValueAnimator;

import com.dmitry.drop.project.model.Post;
import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

/**
 * Created by Dmitry on 22/05/2016.
 *
 * View interfaces are implemented by activities and used by the presenters to interact with the view
 */
public interface WorldMapView extends MvpView {

    int ADD_POST_REQUEST = 1;
    int VIEW_POST_REQUEST = 2;

    void moveCameraToMyLocation();

    void createPost();

    void viewPost(Post post);

    void showPosts(List<Post> posts);

    void addPost(Post post);

    void showPostClickAnim(double latitude, double longitude, ValueAnimator.AnimatorUpdateListener listener);

    void showPostSelector(double latitude, double longitude, List<Post> clickedPosts);

    void showClickPostError(String error);

    void showLoadingPostsError(String error);

    void showAddPostError(String error);

}
