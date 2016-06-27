package com.dmitry.drop.project.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.Animation;

import com.dmitry.drop.project.model.Post;
import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

/**
 * Created by Laptop on 22/05/2016.
 */
public interface WorldMapView extends MvpView {

    static final int ADD_POST_REQUEST = 1;
    static final int VIEW_POST_REQUEST = 2;

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
