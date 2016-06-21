package com.dmitry.drop.project.model;

import java.util.List;

/**
 * Created by Laptop on 20/06/2016.
 */
public interface PostModel {

    void savePost(String annotationText, String mImageFilePath, long mLatitude, long mLongitude, String date);

    void getReplies(long postId, GetRepliesCallback callback);

    Post getPost(long postId);


    interface GetRepliesCallback {
        void onSuccess(List<Reply> replies);

        void onError(String error);
    }
}
