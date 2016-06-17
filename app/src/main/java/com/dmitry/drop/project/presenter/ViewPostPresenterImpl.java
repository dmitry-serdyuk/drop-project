package com.dmitry.drop.project.presenter;

import com.dmitry.drop.project.model.RepliesAsyncLoader;
import com.dmitry.drop.project.model.Reply;
import com.dmitry.drop.project.view.ViewPostView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.List;

/**
 * Created by Laptop on 15/06/2016.
 */
public class ViewPostPresenterImpl extends MvpBasePresenter<ViewPostView> implements ViewPostPresenter {

    private RepliesAsyncLoader repliesLoader;

    @Override
    public void loadReplies() {

        repliesLoader = new RepliesAsyncLoader(
                new RepliesAsyncLoader.RepliesLoaderListener() {

                    @Override public void onSuccess(List<Reply> replies) {

                        if (isViewAttached()) {

                        }
                    }

                    @Override public void onError(Exception e) {

                        if (isViewAttached()) {

                        }
                    }
                });

        repliesLoader.execute();
    }
}
