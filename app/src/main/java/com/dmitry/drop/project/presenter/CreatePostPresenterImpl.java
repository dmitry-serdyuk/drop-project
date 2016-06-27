package com.dmitry.drop.project.presenter;

import com.dmitry.drop.project.model.Post;
import com.dmitry.drop.project.model.PostModel;
import com.dmitry.drop.project.view.CreatePostView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

/**
 * Created by Dmitry on 7/06/2016.
 *
 * Presenter implementations handle view events such as onStart or onClick
 * The presenter interacts with a data model to retrieve data through callbacks
 * This data is then passed to the view along with any loading or error calls
 *
 * The model instance is obtain from an activity
 *
 */
public class CreatePostPresenterImpl extends MvpBasePresenter<CreatePostView> implements CreatePostPresenter {


    private PostModel postModel;

    public CreatePostPresenterImpl(PostModel postModel) {
        this.postModel = postModel;
    }


    @Override
    public void onDropButtonClick(String annotationText, String cameraImageFilePath,
                                  String thumbnailImageFilePath, double latitude,
                                  double longitude, String date) {

        postModel.savePost(annotationText, cameraImageFilePath,
                thumbnailImageFilePath, latitude, longitude, date, new PostModel.SavePostCallback() {
                    @Override
                    public void onSuccess(Post post) {
                        if (isViewAttached()) {
                            getView().returnToWorldMap(post);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        if (isViewAttached()) {
                            getView().showSavePostError(error);
                        }
                    }
                });
    }
}
