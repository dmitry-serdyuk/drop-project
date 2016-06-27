package com.dmitry.drop.project.model;

import com.activeandroid.query.Select;

import java.util.Collections;
import java.util.List;

/**
 * Created by Laptop on 20/06/2016.
 */
public class PostModelImpl implements PostModel {

    @Override
    public void savePost(String annotationText, String cameraImgFilePath,
                         String thumbnailImgFilePath, double latitude,
                         double longitude, String date, SavePostCallback callback) {
        Post post = new Post(annotationText, cameraImgFilePath,
                thumbnailImgFilePath, latitude, longitude, date);
        post.save();

        if (post!=null)
            callback.onSuccess(post);
        else
            callback.onError("Could not save post");
    }

    @Override
    public void getReplies(final Post post, final GetRepliesCallback callback) {
        new RepliesAsyncLoader(
                new RepliesAsyncLoader.RepliesLoaderListener() {
                    @Override
                    public void onSuccess() {
                        List<Reply> replies;
                        if (post != null) {
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
            callback.onError("Cannot load post");
        }
    }

    @Override
    public void getAllPosts(GetAllPostsCallback callback) {
        List<Post> posts = Post.getAll();
        if (posts != null)
            callback.onSuccess(posts);
        else
            callback.onError("Could not get posts.");
    }

    @Override
    public void saveReply(Post post, String author, String annotation, String date, String imageFilePath, SaveReplyCallback callback) {
        Reply reply = new Reply(post, author, annotation, date, imageFilePath);
        reply.save();
        reply = new Select().from(Reply.class).where("id = ?", reply.getId()).executeSingle();
        if(reply != null)
            callback.onSuccess();
        else
            callback.onError("Could not save reply.");
    }

    @Override
    public void like(Post post, boolean liked) {
        post.like(liked);
        post.save();
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
