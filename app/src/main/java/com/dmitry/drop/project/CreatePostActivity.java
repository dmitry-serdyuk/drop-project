package com.dmitry.drop.project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dmitry.drop.project.model.Post;
import com.dmitry.drop.project.model.PostModelImpl;
import com.dmitry.drop.project.presenter.CreatePostPresenter;
import com.dmitry.drop.project.presenter.CreatePostPresenterImpl;
import com.dmitry.drop.project.utility.Constants;
import com.dmitry.drop.project.view.CreatePostView;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.text.DateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dmitry on 7/06/2016.
 *
 * Called by WorldMapView to create a post
 *
 * Requests a file path from the CameraActivity and retrieves a photo from storage
 * An annotation is then added to the photo and a Post object is created
 */
public class CreatePostActivity extends MvpActivity<CreatePostView, CreatePostPresenter>
        implements
        CreatePostView {

    // Constants
    //================================================================================
    private static final int REQUEST_CAMERA_PHOTO = 1;

    // Views
    //================================================================================
    @BindView(R.id.createPost_cameraImg)
    ImageView cameraImg;
    @BindView(R.id.createPost_annotation)
    EditText annotation;
    @BindView(R.id.createPost_dropButton)
    ImageView dropButton;

    // Variables
    //================================================================================
    private double mLongitude;
    private double mLatitude;
    private String mCameraImageFilePath;
    private String mThumbnailImageFilePath;

    //Create an intent for this class with all the necessary extras
    public static Intent createIntent(Context context, double latitude, double longitude) {
        Intent intent = new Intent(context, CreatePostActivity.class);
        intent.putExtra(LATITUDE_EXTRA, latitude);
        intent.putExtra(LONGITUDE_EXTRA, longitude);
        return intent;
    }

    // Constructors
    //================================================================================
    @NonNull
    @Override
    public CreatePostPresenter createPresenter() {
        return new CreatePostPresenterImpl(new PostModelImpl());
    }

    // Activity lifecycle methods
    //================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        ButterKnife.bind(this);

        mLatitude = getIntent().getDoubleExtra(LATITUDE_EXTRA, 0);
        mLongitude = getIntent().getDoubleExtra(LONGITUDE_EXTRA, 0);

        Intent takePhoto = CameraActivity.createIntent(this);
        startActivityForResult(takePhoto, REQUEST_CAMERA_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA_PHOTO && resultCode == RESULT_OK) {
            mCameraImageFilePath = data.getStringExtra(CameraActivity.CAMERA_IMG_FILE_PATH);
            mThumbnailImageFilePath = data.getStringExtra(CameraActivity.THUMBNAIL_IMG_FILE_PATH);

            Glide.with(this).load(mCameraImageFilePath).into(cameraImg);
        } else if (requestCode == REQUEST_CAMERA_PHOTO && resultCode == RESULT_CANCELED) {
            finish();
        }
    }

    @OnClick(R.id.createPost_dropButton)
    public void onDropButtonClick() {
        String annotationText = annotation.getText().toString();
        String date = DateFormat.getInstance().format(new Date());
        if (annotationText.length() == 0) {
            Toast.makeText(this, R.string.write_something_string, Toast.LENGTH_SHORT).show();
        } else {
            presenter.onDropButtonClick(annotationText, mCameraImageFilePath, mThumbnailImageFilePath,
                    mLatitude, mLongitude, date);
        }
    }

    @Override
    public void showSavePostError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void returnToWorldMap(Post post) {
        Intent worldMapIntent = new Intent();
        worldMapIntent.putExtra(LATITUDE_EXTRA, mLatitude);
        worldMapIntent.putExtra(LONGITUDE_EXTRA, mLongitude);
        worldMapIntent.putExtra(POST_EXTRA, post);
        setResult(RESULT_OK, worldMapIntent);
        finish();
    }
}
