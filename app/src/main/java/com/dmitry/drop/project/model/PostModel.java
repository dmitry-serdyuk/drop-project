package com.dmitry.drop.project.model;

import java.util.List;

/**
 * Created by Laptop on 20/06/2016.
 */
public interface PostModel {

    Post savePost(String annotationText, String cameraImgFilePath, String thumbnailImgFilePath,
                  double latitude, double longitude, String date);

    void getReplies(Post post, GetRepliesCallback callback);

    void getPost(long postId, GetPostCallback callback);

    void getAllPosts(GetAllPostsCallback callback);

    void saveReply(Post post, String author, String annotation, String date, String imageFilePath, SaveReplyCallback callback);

    //debug Method
    void delete(long postId);


    //callbacks
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

    interface SaveReplyCallback {
        void onSuccess();

        void onError(String error);
    }

}
