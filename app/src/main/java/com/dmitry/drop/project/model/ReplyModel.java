package com.dmitry.drop.project.model;

/**
 * Created by Laptop on 21/06/2016.
 */
public interface ReplyModel {

    // TODO: Get rid of this and just chuck it in the postModel since it's a child of the postModel anyway
    void saveReply(Post post, String author, String annotation, String timeElapsed, String imageFilePath);

    Reply getReply(long replyId);
}
