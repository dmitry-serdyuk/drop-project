package com.dmitry.drop.project.model;

import java.util.List;

/**
 * Created by Laptop on 20/06/2016.
 */
public interface PostModel {

    // TODO: Make all methods void and follow same process as getReplies

    long savePost(String annotationText, String cameraImgFilePath, String thumbnailImgFilePath,
                  double latitude, double longitude, String date);

    void getReplies(long postId, GetRepliesCallback callback);

    void getPost(long postId, GetPostCallback callback);

    List<Post> getAllPosts();

    //Debug Method
    void delete(long postId);

    interface GetRepliesCallback {
        void onSuccess(List<Reply> replies);

        void onError(String error);
    }

    interface GetPostCallback {
        void onSuccess(Post post);

        void onError(String error);
    }
}
