package com.dmitry.drop.project.model;

import com.activeandroid.query.Select;

import java.util.Collections;
import java.util.List;

/**
 * Created by Laptop on 20/06/2016.
 */
public class PostModelImpl implements PostModel {

    @Override
    public void savePost(String annotationText, String mImageFilePath, long mLatitude, long mLongitude, String date) {

    }

    @Override
    public void getReplies(final long postId, final GetRepliesCallback callback) {
        new RepliesAsyncLoader(
                new RepliesAsyncLoader.RepliesLoaderListener() {

                    @Override
                    public void onSuccess() {
                        List<Reply> replies;
                        if (postId != -1) {
                            Post post = new Select().from(Post.class).where("id = ?", postId).executeSingle();
                            replies = post.replies();
                        } else {
                            replies = Collections.emptyList();
                        }

                        callback.onSuccess(replies);
                    }

                    @Override
                    public void onError(Exception e) {
                        callback.onError(e.getMessage());
                    }
                }).execute();
    }

    @Override
    public Post getPost(long postId) {
        return new Select().from(Post.class).where("id = ?", postId).executeSingle();
    }
}
