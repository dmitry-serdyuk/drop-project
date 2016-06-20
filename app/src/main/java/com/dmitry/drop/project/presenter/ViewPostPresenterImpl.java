package com.dmitry.drop.project.presenter;

import com.activeandroid.query.Select;
import com.dmitry.drop.project.model.Post;
import com.dmitry.drop.project.model.RepliesAsyncLoader;
import com.dmitry.drop.project.model.Reply;
import com.dmitry.drop.project.view.ViewPostView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

/**
 * Created by Laptop on 15/06/2016.
 */
public class ViewPostPresenterImpl extends MvpBasePresenter<ViewPostView> implements ViewPostPresenter {

    private RepliesAsyncLoader repliesLoader;

    @Override
    public void loadReplies() {

        repliesLoader = new RepliesAsyncLoader(
                new RepliesAsyncLoader.RepliesLoaderListener() {

                    @Override
                    public void onSuccess() {
                        if (isViewAttached()) {
                            getView().refreshReplies();
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                        if (isViewAttached()) {

                        }
                    }
                });

        repliesLoader.execute();
    }

    @Override
    public void onSendReplyClick(long postId, String author, String annotation, String date, String imageFilePath) {
        // TODO: Use callback with model
        if (postId != -1) {
            Post post = new Select().from(Post.class).where("id = ?", postId).executeSingle();

            Reply reply = new Reply(post, author, annotation, date, imageFilePath);
            reply.save();
        }


        loadReplies();
    }

    @Override
    public void onSelectImageClick() {
        if (isViewAttached())
            getView().selectImage();
    }

}
