package com.dmitry.drop.project.presenter;


import com.dmitry.drop.project.model.Post;
import com.dmitry.drop.project.model.PostModel;
import com.dmitry.drop.project.view.WorldMapView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

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
                // TODO: Do this instead getView().showPostSelector(List<Post> post)
                getView().viewPost(clickedPost);
        }
    }

    @Override
    public void onStart() {
        // TODO: Change this to callback
        List posts = postModel.getAllPosts();

        if (isViewAttached())
            getView().showPosts(posts);

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
