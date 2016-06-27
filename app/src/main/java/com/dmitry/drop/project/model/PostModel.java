package com.dmitry.drop.project.model;

import java.util.List;

/**
 * Created by Dmitry on 20/06/2016.
 *
 * Post Model Interface declares methods which are implemented by the post model
 * This interface declare methods as well as Callbacks which are used to retrieve data
 * by the presenters.
 */
public interface PostModel {

    // Methods
    //================================================================================
    void savePost(String annotationText, String cameraImgFilePath, String thumbnailImgFilePath,
                  double latitude, double longitude, String date, SavePostCallback callback);

    void getReplies(Post post, GetRepliesCallback callback);

    void getPost(long postId, GetPostCallback callback);

    void getAllPosts(GetAllPostsCallback callback);

    void saveReply(Post post, String author, String annotation, String date, String imageFilePath,
                   SaveReplyCallback callback);

    void like(Post post, boolean liked);

    //debug Method
    void delete(long postId);

    // Callbacks
    //================================================================================
    interface GetRepliesCallback {
        void onSuccess(List<Reply> replies);

        void onError(String error);
    }

    interface GetPostCallback {
        void onSuccess(Post post);

        void onError(String error);
    }

    interface GetAllPostsCallback {
        void onSuccess(List<Post> posts);

        void onError(String error);
    }

    interface SavePostCallback {
        void onSuccess(Post post);

        void onError(String error);
    }

    interface SaveReplyCallback {
        void onSuccess();

        void onError(String error);
    }
}
