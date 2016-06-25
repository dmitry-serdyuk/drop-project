package com.dmitry.drop.project.model;

import com.activeandroid.query.Select;

import java.util.Collections;
import java.util.List;

/**
 * Created by Laptop on 20/06/2016.
 */
public class PostModelImpl implements PostModel {

    @Override
    public Post savePost(String annotationText, String cameraImgFilePath,
                         String thumbnailImgFilePath, double latitude, double longitude, String date) {
        Post post = new Post(annotationText, cameraImgFilePath,
                thumbnailImgFilePath, latitude, longitude, date);
        post.save();

        //return the saved post to be added to world map
        return post;
    }

    @Override
    public void getReplies(final Post post, final GetRepliesCallback callback) {
        final List<Reply> replies = post.replies();
        new RepliesAsyncLoader(
                new RepliesAsyncLoader.RepliesLoaderListener() {
                    @Override
                    public void onSuccess() {
                        //List<Reply> replies = post.replies();
                        /*if (post != null) {
                            replies = post.replies();
                        } else {
                            replies = Collections.emptyList();
                        } */
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
