package com.dmitry.drop.project.model;

import com.activeandroid.query.Select;

/**
 * Created by Laptop on 21/06/2016.
 */
public class ReplyModelImpl implements ReplyModel {
    @Override
    public void saveReply(Post post, String author, String annotation, String timeElapsed, String imageFilePath) {
        Reply reply = new Reply(post, author, annotation, timeElapsed, imageFilePath);
        reply.save();
    }

    @Override
    public Reply getReply(long replyId) {
        return new Select().from(Reply.class).where("id = ?", replyId).executeSingle();
    }
}
