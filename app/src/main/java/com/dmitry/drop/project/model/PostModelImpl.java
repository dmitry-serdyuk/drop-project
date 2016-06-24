package com.dmitry.drop.project.model;

import com.activeandroid.query.Select;

import java.util.Collections;
import java.util.List;

/**
 * Created by Laptop on 20/06/2016.
 */
public class PostModelImpl implements PostModel {

    @Override
    public long savePost(String annotationText, String cameraImgFilePath,
                         String thumbnailImgFilePath, double latitude, double longitude, String date) {
        Post post = new Post(annotationText, cameraImgFilePath,
                thumbnailImgFilePath, latitude, longitude, date);
        post.save();
        return post.getId();
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
    public void getPost(long postId, GetPostCallback callback) {
        Post post = new Select().from(Post.class).where("id = ?", postId).executeSingle();
        if (post != null) {
            callback.onSuccess(post);
        } else {
            callback.onError("Don't have a post with that id.");
        }
    }


    @Override
    public List<Post> getAllPosts() {
        return Post.getAll();
    }

    //Debug
    @Override
    public void delete(long postId) {
        Post post = new Select().from(Post.class).where("id = ?", postId).executeSingle();
        List<Reply> replies = post.replies();
        for (Reply reply : replies) {
            reply.delete();
        }
        post.delete();
    }
}
