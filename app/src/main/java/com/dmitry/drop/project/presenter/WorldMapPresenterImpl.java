package com.dmitry.drop.project.presenter;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.Animation;

import com.dmitry.drop.project.model.Post;
import com.dmitry.drop.project.model.PostModel;
import com.dmitry.drop.project.utility.Constants;
import com.dmitry.drop.project.view.WorldMapView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Dima on 20/05/2016.
 */
public class WorldMapPresenterImpl extends MvpBasePresenter<WorldMapView> implements WorldMapPresenter {

    private PostModel postModel;

    public WorldMapPresenterImpl(PostModel postModel) {
        this.postModel = postModel;
    }

    @Override
    public void onMyLocationClicked() {
        if (isViewAttached())
            getView().moveCameraToMyLocation();
    }

    @Override
    public void onCreatePostClick() {
        if (isViewAttached())
            getView().createPost();
    }

    @Override
    public void onMapClicked(final double latitude, final double longitude) {

        postModel.getAllPosts(new PostModel.GetAllPostsCallback() {
            @Override
            public void onSuccess(List<Post> posts) {
                getClickedPosts(posts, latitude, longitude);
            }

            @Override
            public void onError(String error) {
                if (isViewAttached())
                    getView().showClickPostError(error);
            }
        });
    }

    private void getClickedPosts(List<Post> posts, double latitude, double longitude) {
        final List<Post> clickedPosts = new ArrayList<Post>();
        for (Post post : posts) {
            if (post.isWithinRadius(latitude, longitude)) {
                clickedPosts.add(post);
            }
        }

        if (clickedPosts.size() != 0 && isViewAttached()) {
            if (clickedPosts.size() == 1)
                getView().viewPost(clickedPosts.get(0));
            else
                getView().showPostSelector(latitude, longitude, clickedPosts);
        }
    }

    @Override
    public void onStart() {
        postModel.getAllPosts(new PostModel.GetAllPostsCallback() {
            @Override
            public void onSuccess(List<Post> posts) {
                if (isViewAttached())
                    getView().showPosts(posts);
            }

            @Override
            public void onError(String error) {
                if (isViewAttached())
                    getView().showLoadingPostsError(error);
            }
        });
    }

    @Override
    public void onPostCreated(long postId) {
        postModel.getPost(postId, new PostModel.GetPostCallback() {
            @Override
            public void onSuccess(Post post) {
                if (isViewAttached())
                    getView().addPost(post);
            }

            @Override
            public void onError(String error) {
                if (isViewAttached())
                    getView().showAddPostError(error);
            }
        });
    }
}
